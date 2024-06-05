package com.app.rupyz.ui.organization.profile

import com.app.rupyz.generic.model.profile.profileInfo.createTeam.TeamInfoModel


interface OrgTeamEditListener {
    fun onDeleteTeam(datum: TeamInfoModel, position: Int)
    fun onUpdateTeam(datum: TeamInfoModel, position: Int)
}