/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2016 Incapture Technologies LLC
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
package reflex.suspend;

import java.util.HashMap;
import java.util.Map;

public class SuspendContext {
    public Map<String, Map<String, Object>> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, Map<String, Object>> contexts) {
        this.contexts = contexts;
    }

    public void setResumePoint(String resumePoint) {
        this.resumePoint = resumePoint;
    }

    private String resumePoint = "";
    private Map<String, Map<String, Object>> contexts = new HashMap<String, Map<String, Object>>();

    public void addResumeContext(String nodeId, String contextName, Object value) {
        if (!contexts.containsKey(nodeId)) {
            contexts.put(nodeId, new HashMap<String, Object>());
        }
        contexts.get(nodeId).put(contextName, value);
    }

    public void addResumePoint(String nodeId) {
        this.resumePoint = nodeId;
    }

    public boolean containsResume(String nodeId, String contextName) {
        return (contexts.containsKey(nodeId) && contexts.get(nodeId).containsKey(contextName));
    }

    public Object retrieveResumeContext(String nodeId, String string) {
        if (contexts.containsKey(nodeId)) {
            return contexts.get(nodeId).get(string);
        }
        return null;
    }

    public String getResumePoint() {
        return resumePoint;
    }

}
