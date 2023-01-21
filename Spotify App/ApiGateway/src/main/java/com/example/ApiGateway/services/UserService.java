package com.example.ApiGateway.services;

import com.example.ApiGateway.dtos.*;
import com.example.ApiGateway.exceptions.ConflictException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

@Service
public class UserService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }
    public  String Login(LoginDto loginDto){
        String requestBody ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:service=\"services.IDM.soap\">\n" +
                "    <soap11env:Body>\n" +
                "        <service:login>\n" +
                "            <service:username>"+loginDto.getUsername()+"</service:username>\n" +
                "            <service:password>"+loginDto.getPassword()+"</service:password>\n" +
                "        </service:login>\n" +
                "    </soap11env:Body>\n" +
                "</soap11env:Envelope>";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Document doc = convertStringToDocument(response.body());

        if(doc.getElementsByTagName("tns:loginResult").item(0)!=null){
            String message=doc.getElementsByTagName("tns:loginResult").item(0).getTextContent();
            if(isValid(message)){
                return null;
            }
            else{
                return message;
            }

        }
        return null;

    }
    public  String Logout(LogoutDto logoutDto){
        String requestBody ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:service=\"services.IDM.soap\">\n" +
                "    <soap11env:Body>\n" +
                "        <service:logout>\n" +
                "            <service:jwt_token>"+logoutDto.getJwt_token()+"</service:jwt_token>\n" +
                "        </service:logout>\n" +
                "    </soap11env:Body>\n" +
                "</soap11env:Envelope>";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Document doc = convertStringToDocument(response.body());

        if(doc.getElementsByTagName("tns:logoutResult").item(0)!=null){
            String message=doc.getElementsByTagName("tns:logoutResult").item(0).getTextContent();
            if(isValid(message)){
                return null;
            }
            else{
                return message;
            }

        }
        return null;

    }
    public   boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
    public JSONObject authorize(String jwt_token){
        String requestBody ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:service=\"services.IDM.soap\">\n" +
                "    <soap11env:Body>\n" +
                "        <service:authorize>\n" +
                "            <service:jwt_token>"+jwt_token+"</service:jwt_token>\n" +
                "        </service:authorize>\n" +
                "    </soap11env:Body>\n" +
                "</soap11env:Envelope>";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Document doc = convertStringToDocument(response.body());

        if(doc.getElementsByTagName("tns:authorizeResult").item(0)!=null){
            String message=doc.getElementsByTagName("tns:authorizeResult").item(0).getTextContent();
            if(message.equals("ERROR!")){
                return null;
            }
            else{
                JSONObject user = new JSONObject(message);
                return user;
            }
        }
        return null;

    }
    public  String getUserName(int uid){
        String requestBody ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:service=\"services.IDM.soap\">\n" +
                "    <soap11env:Body>\n" +
                "        <service:get_user>\n" +
                "            <service:UID>"+uid+"</service:UID>\n" +
                "        </service:get_user>\n" +
                "    </soap11env:Body>\n" +
                "</soap11env:Envelope>";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Document doc = convertStringToDocument(response.body());

        if(doc.getElementsByTagName("tns:username").item(0)!=null){
            String username=doc.getElementsByTagName("tns:username").item(0).getTextContent();
            return username;
        }
        return null;
    }
    public String register(LoginDto loginDto){
        String requestBody ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:service=\"services.IDM.soap\">\n" +
                "    <soap11env:Body>\n" +
                "        <service:add_user>\n" +
                "            <service:username>"+loginDto.getUsername()+"</service:username>\n" +
                "            <service:password>"+loginDto.getPassword()+"</service:password>\n" +
                "        </service:add_user>\n" +
                "    </soap11env:Body>\n" +
                "</soap11env:Envelope>";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Document doc = convertStringToDocument(response.body());

        if(doc.getElementsByTagName("tns:add_userResult").item(0)!=null){
            String message=doc.getElementsByTagName("tns:add_userResult").item(0).getTextContent();
            try{
                int userId=Integer.parseInt(message);
                if(userId==-1){
                    return null;
                }
                else{
                    return message;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
        return null;

    }
    private boolean assignUserRole(int userId,String jwtToken,int oldRID, int newRID){

        String requestBody ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:service=\"services.IDM.soap\">\n" +
                "    <soap11env:Body>\n" +
                "        <service:edit_user_role>\n" +
                "            <service:UID>"+userId+"</service:UID>\n" +
                "            <service:RID>"+oldRID+"</service:RID>\n" +
                "            <service:new_RID>"+newRID+"</service:new_RID>\n" +
                "            <service:jwt_token>"+jwtToken+"</service:jwt_token>\n" +
                "        </service:edit_user_role>\n" +
                "    </soap11env:Body>\n" +
                "</soap11env:Envelope>";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Document doc = convertStringToDocument(response.body());

        if(doc.getElementsByTagName("tns:edit_user_roleResult").item(0)!=null){
            String message=doc.getElementsByTagName("tns:edit_user_roleResult").item(0).getTextContent();
            try{

                if(message.equals("false")){
                    return false;
                }
                else if(message.equals("true")){
                    return true;

                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
        return false;


    }

    public boolean registerContentManager(LoginDto loginDto,String jwtToken){

        String requestBody ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:service=\"services.IDM.soap\">\n" +
                "    <soap11env:Body>\n" +
                "        <service:add_user>\n" +
                "            <service:username>"+loginDto.getUsername()+"</service:username>\n" +
                "            <service:password>"+loginDto.getPassword()+"</service:password>\n" +
                "        </service:add_user>\n" +
                "    </soap11env:Body>\n" +
                "</soap11env:Envelope>";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Document doc = convertStringToDocument(response.body());

        if(doc.getElementsByTagName("tns:add_userResult").item(0)!=null){
            String message=doc.getElementsByTagName("tns:add_userResult").item(0).getTextContent();
            try{
                int userId=Integer.parseInt(message);
                if(userId==-1){
                    throw new ConflictException();
                }
                else{

                    boolean success=assignUserRole(userId,jwtToken,3,1);

                    if(success){
                        return true;
                    }
                    else{
                        return false;
                    }

                }
            }catch (NumberFormatException ex){
                ex.printStackTrace();
            }

        }
        return false;

    }
    public int createArtist(ArtistDto artistDto,String jwtToken){

        try{

            JSONObject object = new JSONObject();
            object.put("name",artistDto.getUsername());
            object.put("active",artistDto.getActive());

            StringEntity entity = new StringEntity(object.toString(),
                    ContentType.APPLICATION_JSON);

            org.apache.http.client.HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("http://localhost:8081/api/songcollection/artists");
            request.setHeader("Authorization","Bearer "+jwtToken);
            request.setEntity(entity);

            org.apache.http.HttpResponse response = client.execute(request);

           int statusCode= response.getStatusLine().getStatusCode();


            return  statusCode;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return 406;

    }

    public Integer registerArtist(ArtistDto artistDto, String jwtToken){

        String requestBody ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:service=\"services.IDM.soap\">\n" +
                "    <soap11env:Body>\n" +
                "        <service:add_user>\n" +
                "            <service:username>"+artistDto.getUsername()+"</service:username>\n" +
                "            <service:password>"+artistDto.getPassword()+"</service:password>\n" +
                "        </service:add_user>\n" +
                "    </soap11env:Body>\n" +
                "</soap11env:Envelope>";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8000/"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Document doc = convertStringToDocument(response.body());

        if(doc.getElementsByTagName("tns:add_userResult").item(0)!=null){
            String message=doc.getElementsByTagName("tns:add_userResult").item(0).getTextContent();
            try{
                int userId=Integer.parseInt(message);
                if(userId==-1){
                    throw new ConflictException();
                }
                else{

                    boolean success=assignUserRole(userId,jwtToken,3,2);

                    if(success){

                        Integer responseStatus = createArtist(artistDto,jwtToken);
                        return responseStatus;
                    }
                    else{
                        return null;
                    }

                }
            }catch (NumberFormatException ex){
                ex.printStackTrace();
            }

        }
        return null;

    }

}
