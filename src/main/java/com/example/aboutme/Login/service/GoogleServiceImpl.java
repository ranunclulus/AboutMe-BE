package com.example.aboutme.Login.service;

import com.example.aboutme.Login.MemberConverter;
import com.example.aboutme.Login.dto.SocialInfoDTO;
import com.example.aboutme.Login.jwt.TokenProvider;
import com.example.aboutme.domain.Member;
import com.example.aboutme.domain.constant.Social;
import com.example.aboutme.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleServiceImpl implements GoogleService{
    private final MemberRepository memberRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URL;
    public String getGoogleLogin() {
        return "https://accounts.google.com/o/oauth2/v2/auth?client_id="
                + GOOGLE_CLIENT_ID
                + "&redirect_uri=" + GOOGLE_REDIRECT_URL
                + "&response_type=code" + "&scope=email";
    }
    public SocialInfoDTO.GoogleDTO getGoogleInfo(String code) throws Exception {
        if (code == null) throw new Exception("Failed get authorization code");

        String accessToken = "";
        String refreshToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type"   , "authorization_code");
            params.add("client_id"    , GOOGLE_CLIENT_ID);
            params.add("client_secret", GOOGLE_CLIENT_SECRET);
            params.add("code"         , code);
            params.add("redirect_uri" , GOOGLE_REDIRECT_URL);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://oauth2.googleapis.com/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

            accessToken  = (String) jsonObj.get("access_token");
            refreshToken = (String) jsonObj.get("refresh_token");

            System.out.println(accessToken);
        } catch (Exception e) {
            throw new Exception("API call failed");
        }

        return getUserInfoWithToken(accessToken);
    }

    public SocialInfoDTO.GoogleDTO getUserInfoWithToken(String accessToken) throws Exception {
        //HttpHeader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userInfoEndpointUri = "https://www.googleapis.com/oauth2/v3/userinfo";

        RequestEntity<Void> request = RequestEntity.get(URI.create(userInfoEndpointUri)).headers(headers).build();
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(request, String.class);
        System.out.println(responseEntity);

        // 응답에서 이메일 주소 가져오기
        JsonParser jsonParser = JsonParserFactory.getJsonParser();

        // JSON 문자열을 Map으로 파싱
        Map<String, Object> jsonMap = jsonParser.parseMap(responseEntity.getBody());

        // "sub"와 "email" 키를 사용하여 값을 추출
        String email = (String) jsonMap.get("email");
        System.out.println(email);


        return SocialInfoDTO.GoogleDTO.builder()
                .email(email)
                .build();
    }


    public void saveGoogleMember(SocialInfoDTO.GoogleDTO googleDTO){
        Member newMember = MemberConverter.toMember(googleDTO, Social.GOOGLE);
        Boolean principal = memberRepository.existsByEmail(newMember.getEmail());
        if (principal == false){
            memberRepository.save(newMember);
        }
    }
}
