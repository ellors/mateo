/*
 * The MIT License
 *
 * Copyright 2012 jdmr.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mx.edu.um.mateo.general.dao;

import java.util.HashMap;
import java.util.Map;
import mx.edu.um.mateo.general.model.Empresa;
import mx.edu.um.mateo.general.model.Organizacion;
import mx.edu.um.mateo.general.model.Usuario;
import mx.edu.um.mateo.general.utils.UltimoException;
import mx.edu.um.mateo.inventario.model.Almacen;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jdmr
 */
@Repository
@Transactional
public class EmpresaDao {

    private static final Logger log = LoggerFactory.getLogger(EmpresaDao.class);
    @Autowired
    private SessionFactory sessionFactory;

    public EmpresaDao() {
        log.info("Nueva instancia de EmpresaDao");
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Map<String, Object> lista(Map<String, Object> params) {
        log.debug("Buscando lista de empresas con params {}", params);
        if (params == null) {
            params = new HashMap<>();
        }

        if (!params.containsKey("max")) {
            params.put("max", 10);
        } else {
            params.put("max", Math.min((Integer) params.get("max"), 100));
        }
        if (!params.containsKey("offset")) {
            params.put("offset", 0);
        }
        Criteria criteria = currentSession().createCriteria(Empresa.class);
        Criteria countCriteria = currentSession().createCriteria(Empresa.class);

        if (params.containsKey("organizacion")) {
            criteria.createCriteria("organizacion").add(Restrictions.idEq(params.get("organizacion")));
            countCriteria.createCriteria("organizacion").add(Restrictions.idEq(params.get("organizacion")));
        }

        criteria.setFirstResult((Integer) params.get("offset"));
        criteria.setMaxResults((Integer) params.get("max"));
        params.put("empresas", criteria.list());

        countCriteria.setProjection(Projections.rowCount());
        params.put("cantidad", (Long) countCriteria.list().get(0));

        return params;
    }

    public Empresa obtiene(Long id) {
        Empresa empresa = (Empresa) currentSession().get(Empresa.class, id);
        return empresa;
    }

    public Empresa crea(Empresa empresa, Usuario usuario) {
        Session session = currentSession();
        session.save(empresa);
        Almacen almacen = new Almacen("CENTRAL", empresa);
        session.save(almacen);
        if (usuario != null) {
            usuario.setEmpresa(empresa);
            usuario.setAlmacen(almacen);
            session.update(usuario);
        }
        session.refresh(empresa);
        return empresa;
    }

    public Empresa crea(Empresa empresa) {
        return this.crea(empresa, null);
    }

    public Empresa actualiza(Empresa empresa) {
        return this.actualiza(empresa, null);
    }

    public Empresa actualiza(Empresa empresa, Usuario usuario) {
        Session session = currentSession();
        session.update(empresa);
        if (usuario != null) {
            actualizaUsuario:
            for (Almacen almacen : empresa.getAlmacenes()) {
                usuario.setEmpresa(empresa);
                usuario.setAlmacen(almacen);
                session.update(usuario);
                break actualizaUsuario;
            }
        }
        return empresa;
    }

    public String elimina(Long id) throws UltimoException {
        Criteria criteria = currentSession().createCriteria(Empresa.class);
        criteria.setProjection(Projections.rowCount());
        Long cantidad = (Long) criteria.list().get(0);
        if (cantidad > 1) {
            Empresa empresa = obtiene(id);
            String nombre = empresa.getNombre();
            currentSession().delete(empresa);
            return nombre;
        } else {
            throw new UltimoException("No se puede eliminar porque es el ultimo");
        }
    }
}
