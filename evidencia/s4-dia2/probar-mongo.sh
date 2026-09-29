#!/usr/bin/env bash
API="http://localhost:8080/api/empleados"

resumen() {
  python3 -c '
import json, sys
d = json.load(sys.stdin)
if isinstance(d, dict) and "contenido" in d:
    print("  totalElementos=%s" % d["totalElementos"])
    for e in d["contenido"]:
        dir = e.get("direccion") or {}
        print("  %-10s %-20s %-15s [%s]" % (e["nombre"], e["apellidos"], dir.get("ciudad", "-"), ", ".join(e.get("habilidades") or [])))
elif isinstance(d, list):
    for f in d:
        print("  %-17s empleados=%-2s activos=%-2s promedio=%-9s min=%-9s max=%s" % (
            f["departamento"], f["empleados"], f["activos"], f["salarioPromedio"], f["salarioMinimo"], f["salarioMaximo"]))
else:
    print("  %s" % json.dumps(d, ensure_ascii=False))
'
}

paso() {
  echo
  echo "### $1"
  echo "GET $2"
  curl -s -o /tmp/cuerpo.json -w "HTTP %{http_code}\n" "$API$2"
  resumen < /tmp/cuerpo.json
}

echo "Evidencia Mongo $(date +'%Y-%m-%d %H:%M') $(git config user.name)"
paso "1. Por ciudad, sin acento: queretaro encuentra Querétaro 200" "/buscar?ciudad=queretaro&sort=apellidos,asc"
paso "2. Por habilidad: java (un elemento de la lista) 200" "/buscar?habilidad=java&sort=apellidos,asc"
paso "3. Combinado: Tecnologia en Monterrey 200" "/buscar?departamento=tecnologia&ciudad=monterrey"
paso "4. Una habilidad que nadie tiene 200 vacío" "/buscar?habilidad=cobol"
paso "5. Estadísticas por departamento (agregación) 200" "/estadisticas/departamentos"
