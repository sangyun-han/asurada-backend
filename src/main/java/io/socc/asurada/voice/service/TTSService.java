package io.socc.asurada.voice.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

@Service
public class TTSService {

    // generate .mp3 file and return file name
    public String tts_request(String paramText) {
        String voice_file_name = null;

        try {
            // get id and secret from local file
            File key = new File("C:\\asurada_key\\key.json");
            FileReader fileReader = new FileReader(key);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String jsonString = bufferedReader.readLine();

            if(jsonString == null) {
                throw new NullPointerException("Key for certification doesn't exist.");
            }

            // json parsing
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
            String clientId = (String) jsonObject.get("client_id");
            String clientSecret = (String) jsonObject.get("client_secret");



            String text = URLEncoder.encode(paramText, "UTF-8"); // 13자
            String apiURL = "https://naveropenapi.apigw.ntruss.com/voice-premium/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            // post request
            String postParams = "speaker=nara&volume=0&speed=0&pitch=0&emotion=0&format=mp3&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;

            if(responseCode==200) { // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];
                // 랜덤한 이름으로 mp3 파일 생성
                voice_file_name = Long.valueOf(new Date().getTime()).toString();
                File f = new File(voice_file_name + ".mp3");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read =is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                System.out.println(voice_file_name);
                is.close();
            } else {  // 오류 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return voice_file_name;
    }
}
