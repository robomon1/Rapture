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
package rapture.server.web.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * Allows a Reflex script to set headers in the HttpServletResponse object.
 * 
 * Useful for setting HttpStatus response code, error message etc. 
 * 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseHeaders {
    
    private Map<String, String> headers = new HashMap<String, String>();
    private int statusCode = HttpServletResponse.SC_OK;  
    
    public Map<String, String> getHeaders(){
        return this.headers;
    }
    
    public void setHeaders(Map<String, String> headers){
        this.headers = headers;
    }
    
    public int getStatusCode(){
        return this.statusCode;
    }
    
    public void setStatusCode(int statusCode){
        this.statusCode = statusCode;
    }
    
    public boolean isError(){
        if(this.statusCode >= HttpServletResponse.SC_BAD_REQUEST){
            return true;
        } else {
            return false;
        }
    }
    
    public String getErrorMessage(){
        return this.getHeader("errorMessage");
    }
    
    public String getHeader(String key){
        String value = "";
        if(this.headers.containsKey(key)){
            value = this.headers.get(key);
        }
        return value;
    }
    
}
