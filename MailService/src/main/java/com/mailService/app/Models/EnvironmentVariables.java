package com.mailService.app.Models;





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

    private String angleEmailAddress;

    private String angleEmailPassword;

    private String angleFrontUrl;


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