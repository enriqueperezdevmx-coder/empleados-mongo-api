#!/usr/bin/env bash
API=http://localhost:8080/api/empleados

resumen() { python3 -c '
import json, sys
d = json.load(sys.stdin)
def fila(e):
    return "%-24s %-10s %-20s | %-12s | %8.1f%s" % (
        e["id"], e["nombre"], e["apellidos"], e["departamento"], e["salario"],
        "" if e["activo"] else " INACTIVO"
    )
if isinstance(d, dict) and "contenido" in d:
    print("  pagina=%s tamano=%s totalElementos=%s totalPaginas=%s primera=%s ultima=%s" % (
        d["pagina"], d["tamano"], d["totalElementos"], d["totalPaginas"], d["primera"], d["ultima"]
    ))
    for e in d["contenido"]:
        print(fila(e))
elif isinstance(d, list):
    print("  %d empleados" % len(d))
    for e in d:
        print(fila(e))
else:
    print("  " + json.dumps(d, ensure_ascii=False))
'; }

paso() {
    echo
    echo "### $1"
    echo "GET $2"
    curl -s -o /tmp/cuerpo.json -w 'HTTP %{http_code}\n' "$API$2"
    resumen < /tmp/cuerpo.json
}

echo "Evidencia búsquedas y paginación  $(date +'%Y-%m-%d %H:%M') $(git config user.name)"

paso "1. Primera página por defecto (10 por id) 200" ""
paso "2. Los 5 salarios más altos 200" "?page=0&size=5&sort=salario,desc"
paso "3. La siguiente página de 5 200" "?page=1&size=5&sort=salario,desc"
paso "4. size 1000 se recorta a 50 200" "?size=1000"
paso "5. Buscar departamento ventas (sin importar mayúsculas) 200" "/buscar?departamento=ventas"
paso "6. Buscar texto oscar (sin acento, encuentra Óscar) 200" "/buscar?texto=oscar"
paso "7. Buscar inactivos 200" "/buscar?activo=false"
paso "8. Buscar salario entre 30000 y 50000, del mayor al menor 200" "/buscar?salarioMinimo=30000&salarioMaximo=50000&sort=salario,desc"
paso "9. Filtros combinados: tecnologia activos desde 30000 200" "/buscar?departamento=tecnologia&activo=true&salarioMinimo=30000"
paso "10. Consulta derivada: departamento Finanzas por apellidos 200" "/departamento/Finanzas"
paso "11. Consulta derivada: salarios entre 40000 y 60000 200" "/salarios?minimo=40000&maximo=60000"
paso "12. Ordenar por un campo que no existe 400" "?sort=sueldo"
paso "13. Lo mismo en buscar 400" "/buscar?sort=sueldo"
paso "14. Falta un parametro obligatorio 400" "/salarios?minimo=40000"
paso "15. Un parámetro con tipo equivocado 400" "/buscar?activo=quizas"
