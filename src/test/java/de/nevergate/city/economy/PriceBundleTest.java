package de.nevergate.city.economy;
import java.math.BigInteger;
/** Run with java -ea; checks user-visible bundles against independent exact arithmetic. */
public final class PriceBundleTest {
 public static void main(String[] args){long scale=BuyPrices.SCALE;check(scale/64,64,64,1);check(450*scale,64,1,450);check(2*scale/9,64,9,2);check(15*scale/64,1,5,1);
  int count=0;for(int denominator:new int[]{1,2,3,4,6,8,9,16,27,32,64,108,128,256})for(int numerator=1;numerator<=50;numerator++)for(int stack:new int[]{1,16,64}){
   long micro=(scale/denominator)*numerator;var price=BuyPrices.bundle(micro,stack);if(price==null)throw new AssertionError("Missing bundle");
   BigInteger paid=BigInteger.valueOf(price.units()).multiply(BigInteger.valueOf(scale)),value=BigInteger.valueOf(micro).multiply(BigInteger.valueOf(price.quantity()));
   if(paid.compareTo(value)>0)throw new AssertionError("Rounded upward");if(price.quantity()<1||price.units()<1)throw new AssertionError("Non-positive bundle");count++;
  }System.out.println("Price bundle checks passed: "+(count+4));
 }
 static void check(long micro,int maxStack,int quantity,long units){var p=BuyPrices.bundle(micro,maxStack);if(p==null||p.quantity()!=quantity||p.units()!=units)throw new AssertionError("Wrong bundle: "+p);}
}
