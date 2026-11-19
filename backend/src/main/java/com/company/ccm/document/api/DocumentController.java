package com.company.ccm.document.api;

import com.company.ccm.document.application.DocumentService;
import com.company.ccm.document.domain.DocumentEntity;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentEntity upload(
            @RequestParam String ownerType,
            @RequestParam String ownerId,
            @RequestParam(required = false) String projectId,
            @RequestParam("file") MultipartFile file)
            throws Exception {
        return documentService.upload(ownerType, ownerId, projectId, file);
    }

    @GetMapping
    public List<Map<String, Object>> list(@RequestParam String ownerType, @RequestParam String ownerId) {
        return documentService.list(ownerType, ownerId).stream()
                .map(d -> Map.<String, Object>of(
                        "id", d.getId(),
                        "ownerType", d.getOwnerType(),
                        "ownerId", d.getOwnerId(),
                        "fileName", d.getFileName(),
                        "contentType", d.getContentType(),
                        "sizeBytes", d.getSizeBytes(),
                        "projectId", d.getProjectId() == null ? "" : d.getProjectId(),
                        "uploadedBy", d.getUploadedBy(),
                        "createdAt", d.getCreatedAt().toString()))
                .toList();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) {
        DocumentEntity doc = documentService.get(id);
        InputStream stream = documentService.openStream(doc);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(new InputStreamResource(stream));
    }
}
