enum Direction {Longitude, Latitude}

public class CoordinateLB {
    private Direction direction;
    private Integer degrees;
    private Integer minutes;
    private Integer seconds;

    CoordinateLB(Direction direct){
        this(direct, 0, 0, 0);
    }
    CoordinateLB(Direction direct, Integer degr) throws IllegalArgumentException{
        this(direct, degr, 0, 0);
    }
    CoordinateLB(Direction direct, Integer degr, Integer min) throws IllegalArgumentException{
        this(direct, degr, min, 0);
    }
    CoordinateLB(Direction direct, Integer degr, Integer min, Integer sec) throws IllegalArgumentException{
        setDirection(direct);
        if(!setDegrees(degr))
            throw new IllegalArgumentException("Invalid parameter for degrees");
        else if(!setMinutes(min))
            throw new IllegalArgumentException("Invalid parameter for minutes");
        else if(!setSeconds(sec))
            throw new IllegalArgumentException("Invalid parameter for seconds");
    }

    public Direction getDirection(){
        return direction;
    }
    public void setDirection(Direction dir){
        direction = dir;
    }

    public Integer getDegrees(){
        return degrees;
    }
    public boolean setDegrees(Integer deg){
        if(direction.equals(Direction.Latitude) && -90 <= deg && deg <= 90 ) {
            degrees = deg;
            return true;
        } else if(direction.equals(Direction.Longitude) && -180 <= deg && deg <= 180 ) {
            degrees = deg;
            return true;
        }

        return false;
    }
    public void setDefaultDegrees(){
        degrees = 0;
    }

    public Integer getMinutes(){
        return minutes;
    }
    public boolean setMinutes(Integer m){
        if(m >= 0  && m < 60) {
            minutes = m;
            return true;
        }
        return false;
    }
    public void setDefaultMinutes(){
        minutes = 0;
    }

    public Integer getSeconds(){
        return seconds;
    }
    public boolean setSeconds(Integer s){
        if(s >= 0 && s < 60) {
            seconds = s;
            return true;
        }
        return false;
    }
    public void setDefaultSeconds(){
        seconds = 0;
    }

    public String toString(){
        String directionLetter = directionLetter();
        if(directionLetter == null)
            return null;
        return degrees + "°" + minutes + "′" + seconds + "\" " + directionLetter;
    }

//    Виправлено для випадку, коли у одного із операндів значення degr від'ємне
    public String toStringDec(){
        return getDecCoordinate() + "° " + directionLetter();
    }

//    Виправлено для випадку, коли у одного із операндів значення degr від'ємне
    public CoordinateLB middleWith(CoordinateLB coord){
        return middleBetween(this, coord);
    }

//    метод класу, що повертає:
//    об'єкт типу CoordinateXY, що представляє середню координату між координатами,
//    що представлені двома об'єктами, що отримані як вхідні параметри, або null,
//    якщо об'єкти мають різний напрямок/позицію (Direction)
    public static CoordinateLB middleBetween(CoordinateLB c1, CoordinateLB c2){
        Direction resDir = c1.getDirection();
        if(!c1.getDirection().equals(c2.getDirection()) ) {
            if(c1.getDegrees() == 0 && c1.getMinutes() == 0 && c1.getSeconds() == 0){
                resDir = c2.getDirection();
            } else if(!(c2.getDegrees() == 0 && c2.getMinutes() == 0 && c2.getSeconds() == 0))
                return null;
        }

        double decCoord1 = c1.getDecCoordinate();
        double decCoord2 = c2.getDecCoordinate();
        double midleDec = (decCoord1 + decCoord2) / 2.0;

        int resDegrees = (int) Math.copySign(Math.floor(Math.abs(midleDec)), midleDec);
        int resMin = (int) Math.copySign(Math.floor(Math.abs(midleDec - resDegrees) * 60.0), midleDec);
        int resSec = (int) Math.copySign(Math.floor(Math.abs(midleDec - resDegrees - resMin/60.0) * 3600.0), midleDec);

        return new CoordinateLB(resDir, resDegrees, Math.abs(resMin), Math.abs(resSec));
    }

    private String directionLetter(){
        if(direction.equals(Direction.Latitude)){
            if(degrees > 0)
                return"N\"";
            else if(degrees < 0)
                return"S\"";
            else if(minutes > 0)
                return"N\"";
            else if(minutes < 0)
                return"S\"";
            else if(seconds > 0)
                return"N\"";
            else if(seconds < 0)
                return"S\"";
            return "";
        } else if(direction.equals(Direction.Longitude)){
            if(degrees > 0)
                return"E\"";
            else if(degrees < 0)
                return"W\"";
            else if(minutes > 0)
                return"E\"";
            else if(minutes < 0)
                return"W\"";
            else if(seconds > 0)
                return"E\"";
            else if(seconds < 0)
                return"W\"";
            return "";
        }
        return null;
    }
    private double getDecCoordinate(){
        if(degrees > 0)
            return degrees + minutes/60.0 + seconds/3600.0;
        else
            return degrees - minutes/60.0 - seconds/3600.0;
    }
}
