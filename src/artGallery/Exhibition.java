package artgallery;

public class Exhibition {
    private int id;
    private String title;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;

    public Exhibition(int id, String title, String startDate, String startTime, String endDate, String endTime) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }
}
