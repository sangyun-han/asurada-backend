package io.socc.asurada.voice.controller;

import io.socc.asurada.voice.service.TTSService;
import io.socc.asurada.voice.vo.TTSResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TTSController {

    @Autowired
    TTSService ttsService;

    // Response for GET /api/address
    @RequestMapping(value = "/tts", method = {RequestMethod.GET})
    public TTSResponseVO tts_response(@RequestParam(required = true)String text){
        TTSResponseVO responseVO = new TTSResponseVO();
        responseVO.setMessage(ttsService.tts_request(text));
        return responseVO;
    }
}
