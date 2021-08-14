package model;

public class Comment {
private String name;
private String comment;

    public Comment(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }
}
