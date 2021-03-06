package rapture.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.net.MediaType;

import rapture.common.CallingContext;
import rapture.common.RaptureConstants;
import rapture.common.RaptureURI;
import rapture.common.Scheme;
import rapture.common.api.BlobApi;
import rapture.common.exception.ExceptionToString;
import rapture.common.exception.RaptureException;
import rapture.common.impl.jackson.JacksonUtil;
import rapture.common.model.RaptureExchange;
import rapture.common.model.RaptureExchangeQueue;
import rapture.common.model.RaptureExchangeType;
import rapture.config.ConfigLoader;
import rapture.config.RaptureConfig;
import rapture.kernel.BlobApiImpl;
import rapture.kernel.ContextFactory;
import rapture.kernel.Kernel;

/**
 * Unit tests to verify that BlobApi (et al) calls update ElasticSearch correctly
 * 
 * @author qqqq
 *
 */
public class SearchStandaloneTest {

    public static final String auth = UUID.randomUUID().toString();
    static String blobAuthorityURI = "blob://" + auth;
    public static String saveInitSysConfig;
    public static String saveRaptureRepo;
    public static List<File> files = new ArrayList<>();
    public static RaptureConfig config;
    public static CallingContext callingContext;
    private static BlobApiImpl blobImpl;
    private static final String BLOB_USING_MEMORY = "BLOB {} USING MEMORY {prefix=\"/tmp/B" + auth + "\"}";
    private static final String REPO_USING_MEMORY = "REP {} USING MEMORY {prefix=\"/tmp/" + auth + "\"}";
    private static final String META_USING_MEMORY = "REP {} USING MEMORY {prefix=\"/tmp/M" + auth + "\"}";

    static Logger log = Logger.getLogger(SearchStandaloneTest.class);

    @BeforeClass
    static public void setUp() {
        RaptureConfig.setLoadYaml(false);
        config = ConfigLoader.getConf();
        saveRaptureRepo = config.RaptureRepo;
        saveInitSysConfig = config.InitSysConfig;
        System.setProperty("LOGSTASH-ISENABLED", "false");
        callingContext = new CallingContext();
        callingContext.setUser("dummy");

        config.RaptureRepo = REPO_USING_MEMORY;
        config.InitSysConfig = "NREP {} USING MEMORY { prefix=\"/tmp/" + auth + ".sys.config\"}";
        config.FullTextSearchOn = true;
        config.FullTextSearchIgnoreClusterName = true;

        try {
            Kernel.INSTANCE.restart();
            Kernel.initBootstrap(null, null, true);

            Kernel.getPipeline().getTrusted().registerServerCategory(callingContext, "alpha", "Primary servers");
            Kernel.getPipeline().getTrusted().registerServerCategory(callingContext, "beta", "Secondary servers");

            Kernel.getPipeline().registerExchangeDomain(callingContext, "//main", "EXCHANGE {} USING MEMORY {}");

            RaptureExchange exchange = new RaptureExchange();
            exchange.setName("kernel");
            exchange.setName("kernel");
            exchange.setExchangeType(RaptureExchangeType.FANOUT);
            exchange.setDomain("main");

            List<RaptureExchangeQueue> queues = new ArrayList<RaptureExchangeQueue>();
            RaptureExchangeQueue queue = new RaptureExchangeQueue();
            queue.setName("default");
            queue.setRouteBindings(new ArrayList<String>());
            queues.add(queue);

            exchange.setQueueBindings(queues);

            Kernel.getPipeline().getTrusted().registerPipelineExchange(callingContext, "kernel", exchange);
            Kernel.getPipeline().getTrusted().bindPipeline(callingContext, "alpha", "kernel", "default");

            // Now that the binding is setup, register our server as being part of "alpha"

            Kernel.setCategoryMembership("alpha");

        } catch (RaptureException e) {
            e.printStackTrace();
        }

        Kernel.getAudit().createAuditLog(ContextFactory.getKernelUser(), new RaptureURI(RaptureConstants.DEFAULT_AUDIT_URI, Scheme.LOG).getAuthority(),
                "LOG {} using MEMORY {}");
        Kernel.getLock().createLockManager(ContextFactory.getKernelUser(), "lock://kernel", "LOCKING USING DUMMY {}", "");
        blobImpl = new BlobApiImpl(Kernel.INSTANCE);
        blobImpl.createBlobRepo(callingContext, blobAuthorityURI, BLOB_USING_MEMORY, META_USING_MEMORY);
    }

    @AfterClass
    static public void cleanUp() {
        if (blobImpl.blobRepoExists(callingContext, "blob://dummy")) blobImpl.deleteBlobRepo(callingContext, "blob://dummy");
        for (File temp : files)
            try {
                if (temp.isDirectory()) FileUtils.deleteDirectory(temp);
            } catch (Exception e) {
                log.warn("Cannot delete " + temp);
                // Unable to clean up properly
            }
        ConfigLoader.getConf().InitSysConfig = saveInitSysConfig;
        ConfigLoader.getConf().RaptureRepo = saveRaptureRepo;
    }

    @Test
    public void testBlobApiCallsSearch() {
        Kernel.initBootstrap();
        RaptureURI firstDiv = new RaptureURI.Builder(Scheme.BLOB, "unittest").docPath("English/First").build();
        CallingContext ctxt = ContextFactory.getKernelUser();
        BlobApi blobImpl = Kernel.getBlob();
        blobImpl.createBlobRepo(ctxt, firstDiv.toAuthString(), BLOB_USING_MEMORY, META_USING_MEMORY);

        File pdf = new File("src/test/resources/www-bbc-com.pdf");
        try {
            byte[] content = Files.readAllBytes(pdf.toPath());
            // Use this to test that putting the blob cayuses it to get indexed
            Kernel.getBlob().putBlob(ctxt, firstDiv.toString(), content, MediaType.PDF.toString());
            // use this to just test ESSR
            // e.put(new BlobUpdateObject(firstDiv, content, MediaType.PDF.toString()));
        } catch (IOException e2) {
            fail(pdf.getAbsolutePath() + " : " + ExceptionToString.format(e2));
        }

        try {
            // Tika parsing happens in a separate thread so give it a chance
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
        }

        String query = "blob:*Wigan*";
        rapture.common.SearchResponse res = Kernel.getSearch().searchWithCursor(ctxt, null, 10, query);
        assertNotNull(res.getCursorId());
        assertEquals(JacksonUtil.jsonFromObject(res.getSearchHits()), 1, res.getSearchHits().size());

        blobImpl.deleteBlobRepo(ContextFactory.getKernelUser(), firstDiv.toAuthString());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        query = "blob:*Wigan*";
        res = Kernel.getSearch().searchWithCursor(ctxt, null, 10, query);
        assertNotNull(res.getCursorId());
        assertEquals(0, res.getSearchHits().size());
    }
}
