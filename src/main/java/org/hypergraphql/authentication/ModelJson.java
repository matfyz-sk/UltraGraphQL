package org.hypergraphql.authentication;

import java.util.List;
import java.util.Map;

public class ModelJson {

    private List<String> type;
    private List<String> create;
    private List<String> show;
    private Map<String, Object> props;

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<String> getCreate() {
        return create;
    }

    public void setCreate(List<String> create) {
        this.create = create;
    }

    public List<String> getShow() {
        return show;
    }

    public void setShow(List<String> show) {
        this.show = show;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }
}
