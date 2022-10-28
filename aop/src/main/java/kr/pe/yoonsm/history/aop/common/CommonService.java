package kr.pe.yoonsm.history.aop.common;

import org.springframework.stereotype.Component;

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
     * @param target1
     * @param target2
     * @param targetClass
     * @return
     * @param <T>
     */
    public <T> String diff(T target1, T target2, Class<T> targetClass) {
        StringBuilder sb = new StringBuilder();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(targetClass, Object.class).getPropertyDescriptors()) {
                Object value1 = pd.getReadMethod().invoke(target1);
                Object value2 = pd.getReadMethod().invoke(target2);

                boolean isEqualValue = (value1 == value2) || (value1 != null && value1.equals(value2));
                // 같지 않은 값이 있다면
                if (!isEqualValue) {
                    sb.append(pd.getName()).append(" changed ").append(value1)
                            .append("->").append(value2)
                            .append(" ");
                }
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

}
