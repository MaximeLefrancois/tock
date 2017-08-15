/*
 * Copyright (C) 2017 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.vsct.tock.bot.admin.model

import fr.vsct.tock.bot.admin.user.UserReport
import fr.vsct.tock.bot.engine.user.PlayerId
import java.time.Instant

/**
 *
 */
data class UserSearchResult(val playerId: PlayerId,
                            val applicationIds: MutableSet<String>,
                            val userPreferences: UserPreferencesSearchResult,
                            val userState: UserStateSearchResult,
                            val lastUpdateDate: Instant,
                            val lastActionText: String?) {

    constructor(user: UserReport) : this(
            user.playerId,
            user.applicationIds,
            UserPreferencesSearchResult(user.userPreferences),
            UserStateSearchResult(user.userState),
            user.lastUpdateDate,
            user.lastActionText
    )
}