package io.crate.planner.symbol;

import org.cratedb.DataType;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

public class Null extends Literal<Void> {

    public static final Null INSTANCE = new Null();
    public static final SymbolFactory<Null> FACTORY = new SymbolFactory<Null>() {
        @Override
        public Null newInstance() {
            return INSTANCE;
        }
    };

    @Override
    public DataType valueType() {
        return DataType.NULL;
    }

    @Override
    public SymbolType symbolType() {
        return SymbolType.NULL_LITERAL;
    }

    @Override
    public <C, R> R accept(SymbolVisitor<C, R> visitor, C context) {
        return visitor.visitNullLiteral(this, context);
    }

    @Override
    public Void value() {
        return null;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {

    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {

    }
}
