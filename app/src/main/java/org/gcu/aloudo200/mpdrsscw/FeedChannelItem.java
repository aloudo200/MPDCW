package org.gcu.aloudo200.mpdrsscw;

/*<!-- Created by Andrew Loudon S1426140 -->*/

/**
 * Created by ALOUDO200 on 3/9/2018.
 */

public class FeedChannelItem {

    private String channeltitle;
    private String channeldesc;
    private String channellink;
    private String itemtitle;
    private String itemdesc;
    private String itemlink;
    private String georss;
    private String generator;
    private String pubdate;



    public FeedChannelItem() //channel could be Current Incidents, Roadworks or Planned Roadworks
    {
        channeltitle = "";
        channeldesc = "";
        channellink = "";
        itemtitle = "";
        itemdesc = "";
        itemlink = "";
        georss = "";
        generator = "";
    }

    public FeedChannelItem(String xchtitle, String xchdesc, String xchlink, String xitemtitle, String xitemdesc, String xitemlink, String xgeorss, String xgenerator, String xpubdate)
    {
        channeltitle = xchtitle;
        channeldesc = xchdesc;
        channellink = xchlink;
        itemtitle = xitemtitle;
        itemdesc = xitemdesc;
        itemlink = xitemlink;
        georss = xgeorss;
        generator = xgenerator;
        pubdate = xpubdate;

    }

    //getters and setters below

    public String getChannelTitle()
    {
        return channeltitle;
    }

    public void setChannelTitle(String xchtitle)
    {
        channeltitle = xchtitle;
    }

    public String getChannelDesc()
    {
        return channeldesc;
    }

    public void setChannelDesc(String xchdesc)
    {
        channeldesc = xchdesc;
    }

    public String getChannelLink()
    {
        return channellink;
    }

    public void setChannelLink(String xchlink)
    {
        channellink = xchlink;
    }

    public String getItemTitle () { return itemtitle;}

    public void setItemTitle(String xitemtitle) { itemtitle = xitemtitle; }

    public String getItemDesc () { return itemdesc; }

    public void setItemDesc(String xitemdesc) {itemdesc = xitemdesc; }

    public String getItemLink () { return itemlink; }

    public void setItemLink (String xitemlink) {itemlink = xitemlink;}

    public String getGeorss () { return georss; }

    public void setGeorss (String xgeorss) {georss = xgeorss;}

    public String getGenerator()
    {
        return generator;
    }

    public void setGenerator(String xgenerator)
    {
        generator = xgenerator;
    }

    public String getPubdate() { return pubdate; }

    public void setPubdate (String xpubdate ) {pubdate = xpubdate; }


    public String toString()
    {
        String out;
        out = "\n \n" + itemtitle + " \n \n" + itemdesc + " \n \n" + itemlink + " \n \n" + georss + "\n \n" + pubdate + "\n \n";
        String separator = System.getProperty("line.separator");
        out = out.replaceAll("<br />", separator);
        return out;
    }
}