/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.analyze;

import io.crate.analyze.relations.AnalyzedRelation;
import io.crate.analyze.relations.RelationAnalyzer;
import io.crate.exceptions.RelationAlreadyExists;
import io.crate.expression.symbol.Field;
import io.crate.metadata.Schemas;
import io.crate.metadata.TableIdent;
import io.crate.metadata.TransactionContext;
import io.crate.sql.tree.CreateView;

public final class CreateViewAnalyzer {

    private final Schemas schemas;
    private final RelationAnalyzer relationAnalyzer;

    CreateViewAnalyzer(Schemas schemas, RelationAnalyzer relationAnalyzer) {
        this.schemas = schemas;
        this.relationAnalyzer = relationAnalyzer;
    }

    public AnalyzedStatement analyze(CreateView createView, TransactionContext txnCtx, String defaultSchema) {
        TableIdent name = TableIdent.of(createView.name(), defaultSchema);

        // TODO: We should/could avoid doing this here and instead do it on actual view creation;
        // there in the atomic clusterState update we can make sure there is no race condition between name-check and view creation
        if (schemas.tableExists(name)) {
            throw new RelationAlreadyExists(name);
        }
        if (!createView.replaceExisting() && schemas.viewExists(name)) {
            throw new RelationAlreadyExists(name);
        }
        AnalyzedRelation query = relationAnalyzer.analyzeUnbound(createView.query(), txnCtx, ParamTypeHints.EMPTY);

        if (query.fields().stream().map(Field::outputName).distinct().count() != query.fields().size()) {
            throw new IllegalArgumentException("Query in CREATE VIEW must not have duplicate column names");
        }
        return new CreateViewStmt(name, query, createView.replaceExisting());
    }
}
