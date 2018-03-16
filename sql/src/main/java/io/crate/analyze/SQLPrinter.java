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

import io.crate.analyze.relations.QueriedRelation;
import io.crate.expression.symbol.Field;
import io.crate.expression.symbol.Function;
import io.crate.expression.symbol.Symbol;
import io.crate.expression.symbol.format.SymbolPrinter;
import io.crate.metadata.Reference;

import java.util.List;

public final class SQLPrinter {

    private final Visitor visitor;

    public SQLPrinter(SymbolPrinter symbolPrinter) {
        visitor = new Visitor(symbolPrinter);
    }

    public String format(AnalyzedStatement stmt) {
        StringBuilder sb = new StringBuilder();
        visitor.process(stmt, sb);
        return sb.toString();
    }

    private static class Visitor extends AnalyzedStatementVisitor<StringBuilder, Void> {

        private final SymbolPrinter symbolPrinter;

        public Visitor(SymbolPrinter symbolPrinter) {
            this.symbolPrinter = symbolPrinter;
        }

        @Override
        public Void visitSelectStatement(QueriedRelation relation, StringBuilder sb) {
            QuerySpec querySpec = relation.querySpec();

            sb.append("SELECT ");

            addOutputs(relation, sb, querySpec);
            addFrom(sb, relation);
            addWhere(sb, relation.where());

            return null;
        }

        private void addWhere(StringBuilder sb, WhereClause where) {
            if (where.hasQuery()) {
                sb.append(" WHERE ");
                sb.append(symbolPrinter.printQualified(where.query));
            }
        }

        private void addOutputs(QueriedRelation relation, StringBuilder sb, QuerySpec querySpec) {
            List<Field> fields = relation.fields();
            List<Symbol> outputs = querySpec.outputs();
            for (int i = 0; i < fields.size(); i++) {
                addOutput(sb, fields.get(i), outputs.get(i));

                if (i + 1 < fields.size()) {
                    sb.append(", ");
                }
            }
        }

        private void addOutput(StringBuilder sb, Field field, Symbol output) {
            if (output instanceof Reference) {
                Reference ref = (Reference) output;
                if (ref.column().sqlFqn().equals(field.outputName())) {
                    sb.append(symbolPrinter.printQualified(ref));
                } else {
                    sb.append(symbolPrinter.printQualified(ref));
                    sb.append(" AS ");
                    sb.append(field.outputName());
                }
            } else if (output instanceof Function) {
                sb.append(symbolPrinter.printQualified(output));
                sb.append(" AS ");
                sb.append(field.outputName());
            } else {
                sb.append(field.outputName());
            }
        }

        private static void addFrom(StringBuilder sb, QueriedRelation relation) {
            sb.append(" FROM ");
            sb.append(relation.getQualifiedName());
        }

        @Override
        protected Void visitAnalyzedStatement(AnalyzedStatement stmt, StringBuilder sb) {
            throw new UnsupportedOperationException("Cannot format statement: " + stmt);
        }
    }
}
