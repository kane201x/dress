package com.dino.learn.dto;

import com.dino.learn.entity.ModuleProgress;
import com.dino.learn.entity.Sticker;

import java.util.List;

public class ProgressVO {

    private List<ModuleProgress> completedModules;
    private List<Sticker> stickers;

    public ProgressVO() {
    }

    public ProgressVO(List<ModuleProgress> completedModules, List<Sticker> stickers) {
        this.completedModules = completedModules;
        this.stickers = stickers;
    }

    public List<ModuleProgress> getCompletedModules() {
        return completedModules;
    }

    public void setCompletedModules(List<ModuleProgress> completedModules) {
        this.completedModules = completedModules;
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public void setStickers(List<Sticker> stickers) {
        this.stickers = stickers;
    }
}
