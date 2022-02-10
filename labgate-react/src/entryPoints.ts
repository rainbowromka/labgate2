import SockJS from "sockjs-client";
import {IFrame, messageCallbackType, Stomp} from "@stomp/stompjs";
import Cookies from "universal-cookie";

/**
 * Обработчки точки входа. Передает адресс, точки входа и обратный вызов, для
 * обработки возникающего события на сервере.
 */
export type EntrypointRegistration = {
    route: string
    callback: messageCallbackType
}

/**
 * Устанавливает WebSocket соединение по протоколу STOMP и регистрирует точки
 * входа.
 *
 * @param registrations
 */
export function registerEntryPoints(
    registrations: Array<EntrypointRegistration>)
{
    let cookies = new Cookies();
    let token = cookies.get("Token");
    console.log('Is Authorized: ' + Boolean(token));
    if (token) {
        const stompClient = Stomp.over(() => {
            const socks =
                new SockJS('http://localhost:3002/driverentrypoint', {}, {});
            return socks;
        });

        stompClient.connect({
        }, () => {
            registrations.forEach(
                (registration: EntrypointRegistration) => {stompClient
                    .subscribe(registration.route, registration.callback)
                }
            )
        })
    }
}
