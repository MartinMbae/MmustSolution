package studios.luxurious.mmustsolution;

public class Found {

    String iduid,idtime,idname,idno,scuid,sctime,scname,regno,bookuid,booktime,booktitle,subject,electuid,electime,electtype,model,
            docuid,doctime,doctype,docname,otheruid,othertime,othername,otherno;
    long expiretime;

    public Found() {
    }

    public Found(String iduid,long expiretime,  String idtime, String idname, String idno, String scuid, String sctime, String scname, String regno, String bookuid, String booktime, String booktitle, String subject, String electuid, String electime, String electtype, String model, String docuid,
                 String doctime, String doctype, String docname, String otheruid, String othertime, String othername, String otherno) {
        this.iduid = iduid;
        this.idtime = idtime;
        this.idname = idname;
        this.idno = idno;
        this.scuid = scuid;
        this.expiretime = expiretime;
        this.sctime = sctime;
        this.scname = scname;
        this.regno = regno;
        this.bookuid = bookuid;
        this.booktime = booktime;
        this.booktitle = booktitle;
        this.subject = subject;
        this.electuid = electuid;
        this.electime = electime;
        this.electtype = electtype;
        this.model = model;
        this.docuid = docuid;
        this.doctime = doctime;
        this.doctype = doctype;
        this.docname = docname;
        this.otheruid = otheruid;
        this.othertime = othertime;
        this.othername = othername;
        this.otherno = otherno;
    }

    public long getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(long expiretime) {
        this.expiretime = expiretime;
    }

    public String getIduid() {
        return iduid;
    }

    public void setIduid(String iduid) {
        this.iduid = iduid;
    }

    public String getIdtime() {
        return idtime;
    }

    public void setIdtime(String idtime) {
        this.idtime = idtime;
    }

    public String getIdname() {
        return idname;
    }

    public void setIdname(String idname) {
        this.idname = idname;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getScuid() {
        return scuid;
    }

    public void setScuid(String scuid) {
        this.scuid = scuid;
    }

    public String getSctime() {
        return sctime;
    }

    public void setSctime(String sctime) {
        this.sctime = sctime;
    }

    public String getScname() {
        return scname;
    }

    public void setScname(String scname) {
        this.scname = scname;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getBookuid() {
        return bookuid;
    }

    public void setBookuid(String bookuid) {
        this.bookuid = bookuid;
    }

    public String getBooktime() {
        return booktime;
    }

    public void setBooktime(String booktime) {
        this.booktime = booktime;
    }

    public String getBooktitle() {
        return booktitle;
    }

    public void setBooktitle(String booktitle) {
        this.booktitle = booktitle;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getElectuid() {
        return electuid;
    }

    public void setElectuid(String electuid) {
        this.electuid = electuid;
    }

    public String getElectime() {
        return electime;
    }

    public void setElectime(String electime) {
        this.electime = electime;
    }

    public String getElecttype() {
        return electtype;
    }

    public void setElecttype(String electtype) {
        this.electtype = electtype;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDocuid() {
        return docuid;
    }

    public void setDocuid(String docuid) {
        this.docuid = docuid;
    }

    public String getDoctime() {
        return doctime;
    }

    public void setDoctime(String doctime) {
        this.doctime = doctime;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public String getOtheruid() {
        return otheruid;
    }

    public void setOtheruid(String otheruid) {
        this.otheruid = otheruid;
    }

    public String getOthertime() {
        return othertime;
    }

    public void setOthertime(String othertime) {
        this.othertime = othertime;
    }

    public String getOthername() {
        return othername;
    }

    public void setOthername(String othername) {
        this.othername = othername;
    }

    public String getOtherno() {
        return otherno;
    }

    public void setOtherno(String otherno) {
        this.otherno = otherno;
    }
}
