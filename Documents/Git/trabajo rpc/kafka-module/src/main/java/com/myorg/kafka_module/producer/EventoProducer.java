package com.myorg.kafka_module.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.myorg.kafka_module.dto.EventoDTO;

@Service
public class EventoProducer {

    private static final String TOPIC_EVENTOS = "eventos-solidarios";
    private static final String TOPIC_BAJA_EVENTO = "baja-evento-solidario";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void enviarEvento(EventoDTO evento) {
        kafkaTemplate.send(TOPIC_EVENTOS, evento);
        System.out.println("Evento publicado: " + evento.getNombreEvento());
    }

    public void enviarBajaEvento(EventoDTO evento) {
        kafkaTemplate.send(TOPIC_BAJA_EVENTO, evento);
        System.out.println("Baja de evento publicada: " + evento.getIdEvento());
    }
}
