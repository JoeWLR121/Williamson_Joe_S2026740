package org.me.gcu.williamson_joe_s2026740;
/*Joe Williamson, S2026740*/
public class IncidentClass
{
    private String title;
    private String desc;
    private String geo;

    public IncidentClass()
    {
        title = "";
        geo = "";
        desc = "";

    }

    public IncidentClass(String atitle,String adesc, String ageo)
    {
        title = atitle;
        geo = ageo;
        desc = adesc;

    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String atitle) { title = atitle; }

    public String getGeo()
    {
        return geo;
    }

    public void setGeo(String ageo)
    {
        geo = ageo;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String adesc) { desc = adesc; }

    public String toString()
    {

        String tempT;
        String tempDes;
        String tempGeo;


        if (title == null){
            tempT = "No Title Available";
        }

        else {
            tempT = "Road: " + title + '\n' + '\n';
        }
        tempDes = desc + '\n' + '\n';




        return tempT + tempDes;

    }
}