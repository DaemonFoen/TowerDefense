package org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.UserClient;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.WebClient;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserData{
    WebClient webClient;
    String username;
    UserClient userClient;
}
