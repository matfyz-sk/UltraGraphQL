package org.hypergraphql.mutation.values;

import graphql.Internal;
import graphql.PublicApi;
import graphql.language.*;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import org.joda.time.DateTime;

import java.util.*;
import java.util.function.Consumer;

import static graphql.Assert.assertNotNull;
import static graphql.language.NodeChildrenContainer.newNodeChildrenContainer;
import static graphql.language.NodeUtil.assertNewChildrenAreEmpty;
import static java.util.Collections.emptyMap;

@PublicApi
public class DateTimeValue extends AbstractNode<DateTimeValue> implements ScalarValue<DateTimeValue> {

    private final DateTime value;

    @Internal
    protected DateTimeValue(DateTime value, SourceLocation sourceLocation, List<Comment> comments, IgnoredChars ignoredChars, Map<String, String> additionalData) {
        super(sourceLocation, comments, ignoredChars, additionalData);
        this.value = value;
    }

    /**
     * alternative to using a Builder for convenience
     *
     * @param value of the Float
     */
    public DateTimeValue(DateTime value) {
        this(value, null, new ArrayList<>(), IgnoredChars.EMPTY, emptyMap());
    }

    public DateTime getValue() {
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
    public DateTimeValue withNewChildren(NodeChildrenContainer newChildren) {
        assertNewChildrenAreEmpty(newChildren);
        return this;
    }

    @Override
    public String toString() {
        return "DateTimeValue{" +
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

        DateTimeValue that = (DateTimeValue) o;

        return !(!Objects.equals(value, that.value));

    }

    @Override
    public DateTimeValue deepCopy() {
        return new DateTimeValue(value, getSourceLocation(), getComments(), getIgnoredChars(), getAdditionalData());
    }

    public DateTimeValue transform(Consumer<DateTimeValue.Builder> builderConsumer) {
        DateTimeValue.Builder builder = new DateTimeValue.Builder(this);
        builderConsumer.accept(builder);
        return builder.build();
    }

    @Override
    public TraversalControl accept(TraverserContext<Node> context, NodeVisitor visitor) {
        return TraversalControl.CONTINUE;
    }

    public static DateTimeValue.Builder newDateTimeValue() {
        return new DateTimeValue.Builder();
    }

    public static DateTimeValue.Builder newDateTimeValue(DateTime value) {
        return new DateTimeValue.Builder().value(value);
    }

    public static final class Builder implements NodeBuilder {
        private SourceLocation sourceLocation;
        private DateTime value;
        private List<Comment> comments = new ArrayList<>();
        private IgnoredChars ignoredChars = IgnoredChars.EMPTY;
        private Map<String, String> additionalData = new LinkedHashMap<>();

        private Builder() {
        }

        private Builder(DateTimeValue existing) {
            this.sourceLocation = existing.getSourceLocation();
            this.comments = existing.getComments();
            this.value = existing.getValue();
            this.additionalData = new LinkedHashMap<>(existing.getAdditionalData());
        }


        public DateTimeValue.Builder sourceLocation(SourceLocation sourceLocation) {
            this.sourceLocation = sourceLocation;
            return this;
        }

        public DateTimeValue.Builder value(DateTime value) {
            this.value = value;
            return this;
        }

        public DateTimeValue.Builder comments(List<Comment> comments) {
            this.comments = comments;
            return this;
        }

        public DateTimeValue.Builder ignoredChars(IgnoredChars ignoredChars) {
            this.ignoredChars = ignoredChars;
            return this;
        }

        public DateTimeValue.Builder additionalData(Map<String, String> additionalData) {
            this.additionalData = assertNotNull(additionalData);
            return this;
        }

        public DateTimeValue.Builder additionalData(String key, String value) {
            this.additionalData.put(key, value);
            return this;
        }


        public DateTimeValue build() {
            return new DateTimeValue(value, sourceLocation, comments, ignoredChars, additionalData);
        }
    }
}