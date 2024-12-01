package hotelapp.util;

import java.util.HashMap;

/*
Argument parser check if arguments are passed in correct format if not it returns false.
For eg:
INVALID_ARGS ->-hotels -reviews dataset/hotels dataset/reviews
VALID_ARGS -> -hotels dataset/hotels -reviews dataset/reviews
 */
public class ArgParser {
    private HashMap<String,String> argsMap;

    // -hotel dataSet/hotel -reviews dataset/Reviews
    // -hotel -review

    public ArgParser(){
        this.argsMap = new HashMap<>();
        argsMap.put("-hotels", null);
        argsMap.put("-reviews", null);
        argsMap.put("-threads", "1");
        argsMap.put("-output", null);
    }
    public boolean isEverythingValid(String[] args){
        for(int i=0; i<args.length-1; i++){
            if(checkIfFlag(args[i]) && checkIfFlag(args[i+1])) return false;
            argsMap.put(args[i],args[i+1]);
        }
        return true;
    }

    private boolean checkIfFlag(String arg) {
        if(arg.matches("^-[A-Za-z]+$")){
            return true;
        }
        return false;
    }

    public String getPath(String flag){
        if(!argsMap.containsKey(flag) && argsMap.get(flag)==null){
            throw new IllegalArgumentException("Flag not found");
        }
        return argsMap.get(flag);
    }


}
