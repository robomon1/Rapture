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
package plugin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * There are a list of these in each structure
 * 
 * @author amkimian
 * 
 */
public class AuthorityStructure {
    private String name;

    private List<PluginType> types = new ArrayList<PluginType>();

    private List<PluginIdGen> idgens = new ArrayList<PluginIdGen>();

    private List<PluginQueue> queues = new ArrayList<PluginQueue>();

    private List<PluginOperation> operations = new ArrayList<PluginOperation>();

    private List<PluginIndex> indexes = new ArrayList<PluginIndex>();

    public List<PluginIdGen> getIdGens() {
        return idgens;
    }

    public String getName() {
        return name;
    }

    public List<PluginOperation> getOperations() {
        return operations;
    }

    public List<PluginQueue> getQueues() {
        return queues;
    }

    public List<PluginType> getTypes() {
        return types;
    }

    public void setIdGens(List<PluginIdGen> idgens) {
        this.idgens = idgens;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOperations(List<PluginOperation> operations) {
        this.operations = operations;
    }

    public void setQueues(List<PluginQueue> queues) {
        this.queues = queues;
    }

    public void setTypes(List<PluginType> types) {
        this.types = types;
    }

    public List<PluginIndex> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<PluginIndex> indexes) {
        this.indexes = indexes;
    }

}
