package com.community.ukae.controller.board;

import com.community.ukae.service.s3.S3Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardRestController {

    private final S3Service s3Service;

    public BoardRestController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // 게시글 작성 시 다중 업로드
    @PostMapping("/uploadImages")
    public List<String> uploadImages(@RequestParam("files") List<MultipartFile> files) throws IOException {
        return s3Service.uploadFiles(files);
    }

}