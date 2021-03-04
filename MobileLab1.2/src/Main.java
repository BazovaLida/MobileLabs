public class Main {
    public static void main(String[] args) {
        CoordinateLB coord1 = new CoordinateLB(Direction.Longitude, 12, -31, 43);
        CoordinateLB coord2 = new CoordinateLB(Direction.Longitude, -12, 31, 43);
        System.out.println("Координата 1:  " + coord1.toString());
        System.out.println("Координата 1:  " + coord1.toStringDec());
        System.out.println("Координата 2:  " + coord2.toString());
        System.out.println("Координата 2:  " + coord2.toStringDec());
        CoordinateLB coord3 = coord1.middleWith(coord2);
        System.out.println("Координата 3:  " + coord3.toString() + " (середнє між координатами 1 та 2)");


        CoordinateLB coord4 = new CoordinateLB(Direction.Latitude, -43, 22, 9);
        CoordinateLB coord5 = new CoordinateLB(Direction.Latitude, 87, 41, 2);
        CoordinateLB middleCoord = CoordinateLB.middleBetween(coord4, coord5);

        System.out.println("\nКоордината 4:  " + coord4.toStringDec());
        System.out.println("Координата 5:  " + coord5.toStringDec());
        System.out.println("Середня координата між 4 та 5:  " + middleCoord.toStringDec() + " (отримана від методу класу)");
    }
}

