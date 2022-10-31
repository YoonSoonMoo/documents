package kr.pe.yoonsm.history.aop.common;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-17
 */
@Component
public class CommonService {

    /**
     * 2개의 오브젝트 요소들을 비교하여 틀린 값을 취합한다.
     * @param fromStr
     * @param toStr
     * @param targetClass
     * @return
     * @param <T>
     */
    public <T> String diff(T fromStr, T toStr, Class<T> targetClass) {
        StringBuilder sb = new StringBuilder();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(targetClass, Object.class).getPropertyDescriptors()) {
                Object fromValue = pd.getReadMethod().invoke(fromStr);
                Object toValue = pd.getReadMethod().invoke(toStr);

//                if(fromValue != null && toValue instanceof String ) {
//                    if(!StringUtils.hasLength(toValue.toString())) break;
//                }

                boolean isEqualValue = toValue != null && ((fromValue == toValue) || (fromValue.equals(toValue)));
                // 같지 않은 값이 있다면
                if (!isEqualValue) {
                    sb.append(pd.getName()).append(" changed ").append(fromValue)
                            .append("->").append(toValue)
                            .append(" ");
                }
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

}
