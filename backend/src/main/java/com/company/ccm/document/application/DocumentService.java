package com.company.ccm.document.application;

import com.company.ccm.document.domain.DocumentEntity;
import com.company.ccm.document.infrastructure.DocumentRepository;
import com.company.ccm.document.infrastructure.ObjectStorageService;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.SecurityUtils;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ObjectStorageService objectStorageService;

    public DocumentService(DocumentRepository documentRepository, ObjectStorageService objectStorageService) {
        this.documentRepository = documentRepository;
        this.objectStorageService = objectStorageService;
    }

    @Transactional
    public DocumentEntity upload(String ownerType, String ownerId, String projectId, MultipartFile file)
            throws Exception {
        String key = ownerType + "/" + ownerId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        objectStorageService.upload(key, file.getInputStream(), file.getSize(), file.getContentType());
        DocumentEntity doc = new DocumentEntity();
        doc.setOwnerType(ownerType);
        doc.setOwnerId(ownerId);
        doc.setProjectId(projectId);
        doc.setFileName(file.getOriginalFilename());
        doc.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        doc.setSizeBytes(file.getSize());
        doc.setStorageKey(key);
        doc.setUploadedBy(SecurityUtils.currentUserId());
        return documentRepository.save(doc);
    }

    @Transactional(readOnly = true)
    public List<DocumentEntity> list(String ownerType, String ownerId) {
        return documentRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId);
    }

    @Transactional(readOnly = true)
    public DocumentEntity get(String id) {
        return documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found"));
    }

    public InputStream openStream(DocumentEntity doc) {
        return objectStorageService.download(doc.getStorageKey());
    }
}
