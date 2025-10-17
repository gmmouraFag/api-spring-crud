package com.lucasmartins.mineiracaodados.controller;

import com.lucasmartins.mineiracaodados.model.Turma;
import com.lucasmartins.mineiracaodados.service.TurmaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turmas")
public class TurmaController {

    private final TurmaService service;

    public TurmaController(TurmaService service) {
        this.service = service;
    }

    private <T> ResponseEntity<Map<String, Object>> ok(String msg, T data) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", msg);
        body.put("data", data);
        return ResponseEntity.ok(body);
    }

    private ResponseEntity<Map<String, Object>> created(String location, String msg, Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", msg);
        body.put("data", data);
        return ResponseEntity.created(URI.create(location)).body(body);
    }

    private ResponseEntity<Map<String, Object>> notFound(String msg) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", msg);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    private ResponseEntity<Map<String, Object>> badRequest(String msg) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", msg);
        return ResponseEntity.badRequest().body(body);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@RequestBody(required = false) Turma turma) {
        if (turma == null) return badRequest("Corpo da requisição ausente");
        if (isBlank(turma.getNome()) || isBlank(turma.getCurso()))
            return badRequest("Campos obrigatórios: nome, curso");

        Turma criada = service.criar(turma);
        return created("/api/turmas/" + criada.getId(), "Turma criada com sucesso", criada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        Turma t = service.buscarPorId(id);
        return (t == null) ? notFound("Turma não encontrada: id=" + id)
                : ok("Turma encontrada", t);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodas() {
        List<Turma> lista = service.listarTodas();
        return ok("Lista de turmas", lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable Long id,
                                                         @RequestBody(required = false) Turma turma) {
        if (turma == null) return badRequest("Corpo da requisição ausente");

        if (turma.getNome() != null && isBlank(turma.getNome()))
            return badRequest("Campo 'nome' não pode ser vazio");
        if (turma.getCurso() != null && isBlank(turma.getCurso()))
            return badRequest("Campo 'curso' não pode ser vazio");

        Turma t = service.atualizar(id, turma);
        return (t == null) ? notFound("Turma não encontrada para atualização: id=" + id)
                : ok("Turma atualizada com sucesso", t);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> excluir(@PathVariable Long id) {
        boolean ok = service.excluir(id);
        if (!ok) return notFound("Turma não encontrada para exclusão: id=" + id);
        Map<String, Object> data = Map.of("id", id, "deleted", true);
        return ok("Turma excluída com sucesso", data);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
