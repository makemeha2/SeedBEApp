package com.heyso.SeedBEApp.biz.board.service;

import com.heyso.SeedBEApp.biz.board.dao.BoardFileMapper;
import com.heyso.SeedBEApp.biz.board.model.BoardFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardFileService {
    private final BoardFileMapper boardFileMapper;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.uriPrefix:/files}")
    private String uriPrefix;

    public List<BoardFile> getFiles(Long boardId) {
        return boardFileMapper.selectFilesByBoardId(boardId);
    }

    public Optional<BoardFile> getFile(Long fileId) {
        return Optional.ofNullable(boardFileMapper.selectFileById(fileId));
    }

    @Transactional(rollbackFor = Exception.class)
    public List<BoardFile> saveFiles(Long boardId, List<MultipartFile> files, String rgstId) throws IOException {
        if (files == null || files.isEmpty()) return Collections.emptyList();

        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(base);

        List<BoardFile> toSave = new ArrayList<>();

        for (MultipartFile mf : files) {
            if (mf.isEmpty()) continue;

            String orgName = StringUtils.cleanPath(Objects.requireNonNull(mf.getOriginalFilename()));
            String ext = "";
            int dot = orgName.lastIndexOf('.');
            if (dot >= 0) ext = orgName.substring(dot + 1);

            String storedName = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);
            Path target = base.resolve(storedName).normalize();

            Files.copy(mf.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            BoardFile f = BoardFile.builder()
                    .boardId(boardId)
                    .orgFileNm(orgName)
                    .storedFileNm(storedName)
                    .fileExt(ext)
                    .mimeType(mf.getContentType())
                    .fileSize(mf.getSize())
                    .filePath(target.toString())
                    .useYn("Y")
                    .rgstId(rgstId)
                    .build();

            toSave.add(f);
        }

        if (!toSave.isEmpty()) {
            boardFileMapper.insertBoardFiles(toSave);
        }
        return toSave;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long fileId) throws IOException {
        Optional<BoardFile> f = getFile(fileId);
        if (f.isPresent()) {
            int affected = boardFileMapper.deleteFileById(fileId);
            if (affected > 0) {
                try { Files.deleteIfExists(Paths.get(f.get().getFilePath())); } catch (Exception ignore) {}
            }
        }
    }
}
