/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2011-2016 Incapture Technologies LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * This file is autogenerated and any changes will be overwritten.
 */

package <package>;

import rapture.common.RaptureURI;
<if (sdkname)>
import rapture.common.Scheme;
<else>
import rapture.object.storage.StorageLocationFactory;
<endif>

import org.apache.log4j.Logger;
<if (encodeImport)>
import <encodeImport>;
<endif>

public class <name>PathBuilder {
    <fields; separator="\n">
    
    private static final Logger log = Logger.getLogger(<name>PathBuilder.class);

    public <name>PathBuilder() {
        
    }
    
    public String buildStoragePath() {
        StringBuilder builder = new StringBuilder();
        <adders; separator="\n">
        String ret = builder.toString();

        /**
           Multiple instances of : is illegal eg. foo://bar/baz/foo://bill/bob
           as is : anywhere after / eg. /foo:bar
        **/

        int start = ret.indexOf(":");
        if ((start != ret.lastIndexOf(":")) || (start > ret.indexOf("/"))) {
            String ret2 = ret.replaceAll("/[^/]*:", "");
            log.warn("Illegal storage path. "+ret+" - converted to "+ret2);
            ret = ret2;
        }

        /**
           Eliminate any multiple instances of / except at start or after : which is limited to two.  
           ////foo///bar becomes //foo/bar
           foo:///bar// becomes foo://bar/
           foo///bar becomes foo/bar 
        **/

        if (ret.length() > 0) {
            ret = ret.substring(0, start+2) + (ret.substring(start+2).replaceAll("//+","/"));
            return ret.substring(0, ret.length()-1);
        } else {
            return ret;
        }
    }

<if (encodeMethod)>
    public String encode(String raw) {
        return <encodeMethod>(raw);
    }
<endif>
    
    public RaptureURI buildStorageLocation() {
<if (sdkname)>
        return new RaptureURI.Builder(Scheme.DOCUMENT, "<sdkname>.<name; format="lower">").docPath(buildStoragePath()).build();
<else>
        return StorageLocationFactory.createStorageURI(buildStoragePath(), <name>.class);
<endif>
    }
}
