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

package fr.vsct.tock.nlp.front.shared.config

import fr.vsct.tock.nlp.core.Entity
import fr.vsct.tock.nlp.core.EntityType
import fr.vsct.tock.nlp.core.Intent
import fr.vsct.tock.nlp.core.sample.SampleContext
import fr.vsct.tock.nlp.core.sample.SampleEntity
import fr.vsct.tock.nlp.core.sample.SampleExpression
import fr.vsct.tock.nlp.front.shared.parser.ParseResult
import org.litote.kmongo.Id
import java.time.Instant
import java.util.Locale

/**
 * A sentence with its classification for a given [Locale] and an [ApplicationDefinition].
 */
data class ClassifiedSentence(
    /**
     * The text of the sentence.
     */
    val text: String,
    /**
     * The locale.
     */
    val language: Locale,
    /**
     * The application id.
     */
    val applicationId: Id<ApplicationDefinition>,
    /**
     * Date of creation of this sentence.
     */
    val creationDate: Instant,
    /**
     * Last update date.
     */
    val updateDate: Instant,
    /**
     * The current status of the sentence.
     */
    val status: ClassifiedSentenceStatus,
    /**
     * The current classification of the sentence.
     */
    val classification: Classification,
    /**
     * If not yet validated, the intent probability of the last evaluation.
     */
    val lastIntentProbability: Double?,
    /**
     * If not yet validated, the average entity probability of the last evaluation.
     */
    val lastEntityProbability: Double?,
    /**
     * The last usage date (for a real user) if any.
     */
    val lastUsage: Instant? = null,
    /**
     * The total number of uses of this sentence.
     */
    val usageCount: Long = 0
) {

    constructor(
        query: ParseResult,
        language: Locale,
        applicationId: Id<ApplicationDefinition>,
        intentId: Id<IntentDefinition>,
        lastIntentProbability: Double,
        lastEntityProbability: Double
    )
            : this(
        query.retainedQuery,
        language,
        applicationId,
        Instant.now(),
        Instant.now(),
        ClassifiedSentenceStatus.inbox,
        Classification(query, intentId),
        lastIntentProbability,
        lastEntityProbability
    )

    /**
     * Check if the sentence has the same content (status, creation & update dates excluded)
     */
    fun hasSameContent(sentence: ClassifiedSentence?): Boolean {
        return this == sentence?.copy(
            status = status,
            creationDate = creationDate,
            updateDate = updateDate,
            lastIntentProbability = lastIntentProbability,
            lastEntityProbability = lastEntityProbability
        )
    }

    /**
     * Build an expression from this sentence.
     *
     * @param intentProvider intent id -> intent provider
     * @param entityTypeProvider entity type name -> entity type provider
     */
    fun toSampleExpression(
        intentProvider: (Id<IntentDefinition>) -> Intent,
        entityTypeProvider: (String) -> EntityType?
    ): SampleExpression {
        return SampleExpression(
            text,
            intentProvider.invoke(classification.intentId),
            classification.entities.mapNotNull {
                toSampleEntity(it, entityTypeProvider)
            },
            SampleContext(language)
        )
    }

    private fun toSampleEntity(entity: ClassifiedEntity, entityTypeProvider: (String) -> EntityType?): SampleEntity? {
        return entityTypeProvider
            .invoke(entity.type)
            ?.run {
                SampleEntity(
                    Entity(this, entity.role),
                    entity.subEntities.mapNotNull { toSampleEntity(it, entityTypeProvider) },
                    entity.start,
                    entity.end
                )
            }
    }
}