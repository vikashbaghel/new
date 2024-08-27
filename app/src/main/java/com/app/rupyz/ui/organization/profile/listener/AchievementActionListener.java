package com.app.rupyz.ui.organization.profile.listener;

import com.app.rupyz.generic.model.profile.achievement.AchievementData;

public interface AchievementActionListener {
    void onEditAchievement(AchievementData achievement, int position);

    void onDeleteAchievement(AchievementData achievement, int position);
}
