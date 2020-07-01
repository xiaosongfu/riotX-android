/*
 * Copyright (c) 2020 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.matrix.android.internal.session.profile

import im.vector.matrix.android.internal.network.executeRequest
import im.vector.matrix.android.internal.task.Task
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

internal abstract class SetAvatarUrlTask : Task<SetAvatarUrlTask.Params, Unit> {
    data class Params(
            val userId: String,
            val newAvatarUrl: String
    )
}

internal class DefaultSetAvatarUrlTask @Inject constructor(
        private val profileAPI: ProfileAPI,
        private val eventBus: EventBus) : SetAvatarUrlTask() {

    override suspend fun execute(params: Params) {
        return executeRequest(eventBus) {
            val body = SetAvatarUrlBody(
                    avatarUrl = params.newAvatarUrl
            )
            apiCall = profileAPI.setAvatarUrl(params.userId, body)
        }
    }
}
