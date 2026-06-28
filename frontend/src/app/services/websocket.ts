import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject, Observable } from 'rxjs';
import { ConfigService } from '../config.service';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  private client!: Client;
  private connected = false;

  constructor(private config: ConfigService) {}

  connect(): void {
    if (this.connected) return;
    this.client = new Client({
      webSocketFactory: () => new SockJS(`${this.config.apiUrl}/ws`),
      reconnectDelay: 5000,
    });
    this.client.activate();
    this.connected = true;
  }

  subscribeToSlots(doctorId: number): Observable<any> {
    const subject = new Subject<any>();
    const checkAndSubscribe = () => {
      if (this.client?.connected) {
        this.client.subscribe(`/topic/slots/${doctorId}`, (msg: IMessage) => {
          subject.next(JSON.parse(msg.body));
        });
      } else {
        setTimeout(checkAndSubscribe, 500);
      }
    };
    checkAndSubscribe();
    return subject.asObservable();
  }

  disconnect(): void {
    this.client?.deactivate();
    this.connected = false;
  }
}
