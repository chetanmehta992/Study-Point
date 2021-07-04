package app.aacr.studypoint.pojo;

public class ClgstudentInfo {
    String type = "College Student";
    String profile = "";
    String name = "";
    String age = "";
    String gender = "";
    String university = "";
    String branch = "";
    String yog = "";
    String security_code = "" ;
    String Id_proof = "";
    String verified ="false";
    String status = "Available";
    String online = "true";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public ClgstudentInfo(String profile, String name, String age, String gender, String university, String branch, String yog, String security_code, String id_proof) {
        this.profile = profile;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.university = university;
        this.branch = branch;
        this.yog = yog;
        this.security_code = security_code;
        Id_proof = id_proof;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getYog() {
        return yog;
    }

    public void setYog(String yog) {
        this.yog = yog;
    }

    public String getSecurity_code() {
        return security_code;
    }

    public void setSecurity_code(String security_code) {
        this.security_code = security_code;
    }

    public String getId_proof() {
        return Id_proof;
    }

    public void setId_proof(String id_proof) {
        Id_proof = id_proof;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
