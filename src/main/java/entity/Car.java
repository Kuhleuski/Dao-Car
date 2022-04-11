package entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor

@MyTable("car")
public class Car {
    @MyColumn("id")
    private int id;
    @MyColumn("carname")
    private String carname;
    @MyColumn("color")
    private String color;
    @MyColumn("price")
    private int price;
}

@interface MyColumn {
    String value();
}
@interface MyTable {
    String value();
}