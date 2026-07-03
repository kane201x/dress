package com.dino.learn.service;

import com.dino.learn.dto.ProgressVO;
import com.dino.learn.entity.ModuleProgress;
import com.dino.learn.entity.Sticker;
import com.dino.learn.repository.ModuleProgressRepository;
import com.dino.learn.repository.StickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgressService {

    private final ModuleProgressRepository moduleProgressRepository;
    private final StickerRepository stickerRepository;

    public ProgressService(ModuleProgressRepository moduleProgressRepository,
                           StickerRepository stickerRepository) {
        this.moduleProgressRepository = moduleProgressRepository;
        this.stickerRepository = stickerRepository;
    }

    @Transactional
    public ModuleProgress completeModule(Long userId, String moduleKey) {
        return moduleProgressRepository.findByUserIdAndModuleKey(userId, moduleKey)
                .orElseGet(() -> {
                    ModuleProgress progress = new ModuleProgress();
                    progress.setUserId(userId);
                    progress.setModuleKey(moduleKey);
                    progress.setCompleted(true);
                    return moduleProgressRepository.save(progress);
                });
    }

    public List<ModuleProgress> getCompletedModules(Long userId) {
        return moduleProgressRepository.findByUserId(userId);
    }

    @Transactional
    public Sticker unlockSticker(Long userId, String stickerKey) {
        Sticker sticker = new Sticker();
        sticker.setUserId(userId);
        sticker.setStickerKey(stickerKey);
        return stickerRepository.save(sticker);
    }

    public List<Sticker> getStickers(Long userId) {
        return stickerRepository.findByUserId(userId);
    }

    public ProgressVO getProgress(Long userId) {
        List<ModuleProgress> modules = getCompletedModules(userId);
        List<Sticker> stickers = getStickers(userId);
        return new ProgressVO(modules, stickers);
    }
}
