package projects.etrkk5.todoapp.item;

/**
 * Created by EsrefTurkok on 27.05.2018.
 */

public class item {
    private String title;
    private String date;
    private String time;
    private String docsRef;

    public item(String title, String date, String time, String docsRef){
        this.title = title;
        this.date = date;
        this.time = time;
        this.docsRef = docsRef;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDocsRef() {
        return docsRef;
    }

    public void setDocsRef(String docsRef) {
        this.docsRef = docsRef;
    }
}
