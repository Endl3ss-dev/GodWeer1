package org.privates.client.managers.module.impl.combat;

import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.module.impl.combat.auraelement.impl.AiRotation;


public class AiData {

    private final AuraNew AuraNew;
    private AiRotation aiRotationInstance;

    public AiData(AuraNew AuraNew) {
        this.AuraNew = AuraNew;
    }

    public void setAiRotationInstance(AiRotation aiRotation) {
        this.aiRotationInstance = aiRotation;
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (AuraNew.isEnabled() && aiRotationInstance != null && event.getTarget() != null) {
            aiRotationInstance.recordAttackEvent();
        }
    }

    public void saveCurrentSession() {
        if (aiRotationInstance != null) {
            aiRotationInstance.saveSession();
        }
    }

    public void learnFromCurrentData() {
        if (aiRotationInstance != null) {
            aiRotationInstance.learnFromSamples();
        }
    }

    public void clearAllData() {
        if (aiRotationInstance != null) {
            aiRotationInstance.clearAllData();
        }
    }

    public String getStatisticsReport() {
        if (aiRotationInstance == null) {
            return "AI Neuro-Rotation not initialized";
        }
        var stats = aiRotationInstance.getStatistics();
        if (stats == null) return "Statistics unavailable.";
        return String.format(
                "=== AI Neuro-Core Statistics ===\nTotal Samples: %d\nTotal Rotations: %d\nSuccessful Hits: %d\nHit Rate: %.2f%%\nSessions: %d",
                stats.totalSamples, stats.totalRotations, stats.successfulHits, stats.hitRate * 100, stats.totalSessions
        );
    }

    public void exportProfile(String filename) {
        if (aiRotationInstance != null) {
        }
    }
}