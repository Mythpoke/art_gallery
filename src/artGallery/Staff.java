package artGallery;

public class Staff {
    private int id;
    private String name;
    private String position;
    private String hireDate;
    private double salary;

    public Staff(int id, String name, String position, String hireDate, double salary) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
