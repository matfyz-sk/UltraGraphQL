package org.hypergraphql.mutation.values;

import graphql.Internal;
import graphql.PublicApi;
import graphql.language.*;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

import static graphql.Assert.assertNotNull;
import static graphql.language.NodeChildrenContainer.newNodeChildrenContainer;
import static graphql.language.NodeUtil.assertNewChildrenAreEmpty;
import static java.util.Collections.emptyMap;

@PublicApi
public class DecimalValue extends AbstractNode<DecimalValue> implements ScalarValue<DecimalValue> {

    private final BigDecimal value;

    @Internal
    protected DecimalValue(BigDecimal value, SourceLocation sourceLocation, List<Comment> comments, IgnoredChars ignoredChars, Map<String, String> additionalData) {
        super(sourceLocation, comments, ignoredChars, additionalData);
        this.value = value;
    }

    /**
     * alternative to using a Builder for convenience
     *
     * @param value of the Float
     */
    public DecimalValue(BigDecimal value) {
        this(value, null, new ArrayList<>(), IgnoredChars.EMPTY, emptyMap());
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public NodeChildrenContainer getNamedChildren() {
        return newNodeChildrenContainer().build();
    }

    @Override
    public DecimalValue withNewChildren(NodeChildrenContainer newChildren) {
        assertNewChildrenAreEmpty(newChildren);
        return this;
    }

    @Override
    public String toString() {
        return "BigDecimalValue{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean isEqualTo(Node o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DecimalValue that = (DecimalValue) o;

        return Objects.equals(value, that.value);

    }

    @Override
    public DecimalValue deepCopy() {
        return new DecimalValue(value, getSourceLocation(), getComments(), getIgnoredChars(), getAdditionalData());
    }

    public DecimalValue transform(Consumer<DecimalValue.Builder> builderConsumer) {
        DecimalValue.Builder builder = new DecimalValue.Builder(this);
        builderConsumer.accept(builder);
        return builder.build();
    }

    @Override
    public TraversalControl accept(TraverserContext<Node> context, NodeVisitor visitor) {
        return TraversalControl.CONTINUE;
    }

    public static DecimalValue.Builder newBigDecimalValue() {
        return new DecimalValue.Builder();
    }

    public static DecimalValue.Builder newBigDecimalValue(BigDecimal value) {
        return new DecimalValue.Builder().value(value);
    }

    public static final class Builder implements NodeBuilder {
        private SourceLocation sourceLocation;
        private BigDecimal value;
        private List<Comment> comments = new ArrayList<>();
        private IgnoredChars ignoredChars = IgnoredChars.EMPTY;
        private Map<String, String> additionalData = new LinkedHashMap<>();

        private Builder() {
        }

        private Builder(DecimalValue existing) {
            this.sourceLocation = existing.getSourceLocation();
            this.comments = existing.getComments();
            this.value = existing.getValue();
            this.additionalData = new LinkedHashMap<>(existing.getAdditionalData());
        }


        public DecimalValue.Builder sourceLocation(SourceLocation sourceLocation) {
            this.sourceLocation = sourceLocation;
            return this;
        }

        public DecimalValue.Builder value(BigDecimal value) {
            this.value = value;
            return this;
        }

        public DecimalValue.Builder comments(List<Comment> comments) {
            this.comments = comments;
            return this;
        }

        public DecimalValue.Builder ignoredChars(IgnoredChars ignoredChars) {
            this.ignoredChars = ignoredChars;
            return this;
        }

        public DecimalValue.Builder additionalData(Map<String, String> additionalData) {
            this.additionalData = assertNotNull(additionalData);
            return this;
        }

        public DecimalValue.Builder additionalData(String key, String value) {
            this.additionalData.put(key, value);
            return this;
        }


        public DecimalValue build() {
            return new DecimalValue(value, sourceLocation, comments, ignoredChars, additionalData);
        }
    }
}