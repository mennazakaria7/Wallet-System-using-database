public class Admin {

    private int id;
    private String AdminName;
    private String password;



    public Admin(String name,String password){

        this.AdminName=name;
        this.password=password;

    }

    public Admin(int id, String name, String password) {

        this.id=id;
        this.AdminName=name;
        this.password=password;
    }


    public int GetAdmioId(){
    return id;
}
    public String GetAdminUserName(){
        return AdminName;
    }

    public String GetAdminPassword(){
        return password;
    }
    public void SetAdminId(int id){
       this.id=id;
    }

    public void SetAdminUserName(String AdminNme){
        this.AdminName=AdminNme;
    }
    public void SetAdminPassword(String password){
        this.password=password;
    }




}