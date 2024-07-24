package com.example.fleemarket;

public class User
{
    private String name,lname,phone,username;

    public User() { }
    public User( String username,String name, String lname, String phone) {
        this.username = username;
        this.name = name;
        this.lname = lname;
        this.phone = phone;

    }

    /**
     * check if the details not null
     * @return boolean if the details not null
     */
    public boolean detailsOk()
    {
        if (this.username!=null&&this.name!=null&&this.lname!=null&&this.phone!=null)

        {
            if (!this.username.equals("")&&!this.name.equals("")&&!this.lname.equals("")&&!this.phone.equals(""))
            {
                return true;
            }
        }
        return false;
    }
    public String getName() {
    return name;
}

    public String getLname() {
        return lname;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" + "name='" + name + '\'' + ", lname='" + lname + '\'' + ", phone='" + phone + '\'' + ", username='" + username + '\'' + '}';
    }
}
