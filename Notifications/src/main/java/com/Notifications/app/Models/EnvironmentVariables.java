package com.Notifications.app.Models;





import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvironmentVariables {

    private String jTokenKey;

    public boolean checkIfNotNull(){
        for(Field field : this.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try {
                if(field.get(this) == null){
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error checking null for field "+field.getName(),e);
            }
        }
        return true;
    }

}