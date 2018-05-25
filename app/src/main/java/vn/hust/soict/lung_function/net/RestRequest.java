package vn.hust.soict.lung_function.net;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import vn.hust.soict.lung_function.model.LungFunction;

public class RestRequest {
    private RestTemplate mRestTemplate;
    private HttpHeaders mHttpHeaders;
    private MultiValueMap<String, String> params;


    public RestRequest() {
        this.mRestTemplate = new RestTemplate();
    }

    public void setHeaders() {
        mHttpHeaders = new HttpHeaders();
        mHttpHeaders.setAccept(Arrays.asList(MediaType.ALL));
        params = new LinkedMultiValueMap<String, String>();
        mHttpHeaders.add(WebGlobal.HEADER_CONTENT_TYPE,WebGlobal.CONTENT_TYPE);
        mHttpHeaders.add(WebGlobal.HEADER_M2M_ORIGIN, WebGlobal.PASS_M2M);
    }

    private String encodeFileToBase64Binary(String filename) throws IOException {
        File file = new File(filename);
        byte[] bytes = loadFile(file);
        String encoded = Base64.encodeToString(bytes,0);
        return encoded;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();

        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < numRead) {
            throw new IOException("can not read file");
        }

        is.close();
        return bytes;
    }

    public JSONObject postResponse(String url, String filename,String userId) throws MalformedURLException, IOException {
        LungFunction lungFunction = new LungFunction();
//        final File path =
//                Environment.getExternalStoragePublicDirectory
//                        (
//                                //Environment.DIRECTORY_PICTURES
//                                Environment.DIRECTORY_DCIM + "/binh/"
//                        );
//        if(!path.exists()) {
//            // Make it, if it doesn't exit
//            path.mkdirs();
//        }
        String fileContent = encodeFileToBase64Binary(filename);
        fileContent = fileContent.replace("\n","");
//        byte[] decoded = Base64.decode(fileContent, 0);
//        try {
//            File file2 = new File(path,"filename.wav");
//            FileOutputStream os = new FileOutputStream(file2, true);
//            os.write(decoded);
//            os.close();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "CreateRecord");
            parameters.put("user_id", userId);
            parameters.put("record", fileContent);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        URL myUrl = new URL(url);
        urlConnection = (HttpURLConnection) myUrl.openConnection();
        try {
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty(WebGlobal.HEADER_M2M_ORIGIN, WebGlobal.PASS_M2M);
            urlConnection.setRequestProperty(WebGlobal.HEADER_ACCEPT,WebGlobal.REGISTER_CONTENT_TYPE);
            urlConnection.setRequestProperty(WebGlobal.HEADER_CONTENT_TYPE,WebGlobal.REGISTER_CONTENT_TYPE);
            urlConnection.connect();
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(parameters.toString());
            long startTime = System.currentTimeMillis();
            StringBuilder response = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Time measure: " + elapsedTime);
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output + "\n");
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse;
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
            return null;
        }
        finally {
            urlConnection.disconnect();
        }
    }
}