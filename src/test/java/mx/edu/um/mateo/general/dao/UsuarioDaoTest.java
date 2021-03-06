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
import java.util.List;
import java.util.Map;
import mx.edu.um.mateo.general.model.Empresa;
import mx.edu.um.mateo.general.model.Organizacion;
import mx.edu.um.mateo.general.model.Rol;
import mx.edu.um.mateo.general.model.Usuario;
import mx.edu.um.mateo.general.utils.UltimoException;
import mx.edu.um.mateo.inventario.model.Almacen;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jdmr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mateo.xml", "classpath:security.xml"})
@Transactional
public class UsuarioDaoTest {

    private static final Logger log = LoggerFactory.getLogger(UsuarioDaoTest.class);
    @Autowired
    private UsuarioDao instance;
    @Autowired
    private RolDao rolDao;
    @Autowired
    private OrganizacionDao organizacionDao;
    @Autowired
    private EmpresaDao empresaDao;

    public UsuarioDaoTest() {
    }

    /**
     * Test of lista method, of class UsuarioDao.
     */
    @Test
    public void debieraObtenerListaDeUsuarios() {
        log.debug("Debiera obtener lista de usuarios");

        Organizacion organizacion = new Organizacion("TEST01", "TEST01", "TEST01");
        organizacion = organizacionDao.crea(organizacion);

        Rol rol = new Rol("ROLE_TEST");
        rol = rolDao.crea(rol);
        for (int i = 0; i < 20; i++) {
            Usuario usuario = new Usuario("test-" + i + "@test.com", "test-" + i, "TEST " + i, "TEST");
            Long almacenId = 0l;
            actualizaUsuario:
            for (Empresa empresa : organizacion.getEmpresas()) {
                for (Almacen almacen : empresa.getAlmacenes()) {
                    almacenId = almacen.getId();
                    break actualizaUsuario;
                }
            }
            instance.crea(usuario, almacenId, new String[]{rol.getAuthority()});
        }

        Map<String, Object> params = null;
        Map<String, Object> result = instance.lista(params);
        List<Usuario> usuarios = (List<Usuario>) result.get("usuarios");
        Long cantidad = (Long) result.get("cantidad");
        assertEquals(10, usuarios.size());
        assertTrue(20 <= cantidad);
    }

    /**
     * Test of obtiene method, of class UsuarioDao.
     */
    @Test
    public void debieraObtenerUsuario() {
        log.debug("Debiera obtener usuario");
        Organizacion organizacion = new Organizacion("TEST01", "TEST01", "TEST01");
        organizacion = organizacionDao.crea(organizacion);
        Rol rol = new Rol("ROLE_TEST");
        rol = rolDao.crea(rol);
        Usuario usuario = new Usuario("test-01@test.com", "test-01", "TEST1", "TEST");
        Long almacenId = 0l;
        actualizaUsuario:
        for (Empresa empresa : organizacion.getEmpresas()) {
            for (Almacen almacen : empresa.getAlmacenes()) {
                almacenId = almacen.getId();
                break actualizaUsuario;
            }
        }
        usuario = instance.crea(usuario, almacenId, new String[]{rol.getAuthority()});
        Long id = usuario.getId();

        Usuario result = instance.obtiene(id);
        assertEquals(usuario, result);
        assertTrue(result.getRoles().contains(rol));
    }

    /**
     * Test of crea method, of class UsuarioDao.
     */
    @Test
    public void debieraCrearUsuario() {
        log.debug("Debiera crear usuario");
        Organizacion organizacion = new Organizacion("TEST01", "TEST01", "TEST01");
        organizacion = organizacionDao.crea(organizacion);
        Rol rol = new Rol("ROLE_TEST");
        rol = rolDao.crea(rol);
        Usuario usuario = new Usuario("test-01@test.com", "test-01", "TEST1", "TEST");
        Long almacenId = 0l;
        actualizaUsuario:
        for (Empresa empresa : organizacion.getEmpresas()) {
            for (Almacen almacen : empresa.getAlmacenes()) {
                almacenId = almacen.getId();
                break actualizaUsuario;
            }
        }
        usuario = instance.crea(usuario, almacenId, new String[]{rol.getAuthority()});
        Long id = usuario.getId();
        assertNotNull(id);
    }

    /**
     * Test of actualiza method, of class UsuarioDao.
     */
    @Test
    public void debieraActualizarUsuario() {
        log.debug("Debiera actualizar usuario");
        Organizacion organizacion = new Organizacion("TEST01", "TEST01", "TEST01");
        organizacion = organizacionDao.crea(organizacion);
        Rol rol = new Rol("ROLE_TEST");
        rol = rolDao.crea(rol);
        Usuario usuario = new Usuario("test-01@test.com", "test-01", "TEST1", "TEST");
        Long almacenId = 0l;
        actualizaUsuario:
        for (Empresa empresa : organizacion.getEmpresas()) {
            for (Almacen almacen : empresa.getAlmacenes()) {
                almacenId = almacen.getId();
                break actualizaUsuario;
            }
        }
        usuario = instance.crea(usuario, almacenId, new String[]{rol.getAuthority()});
        Long id = usuario.getId();
        assertNotNull(id);

        usuario.setNombre("PRUEBA");
        instance.actualiza(usuario, almacenId, new String[]{rol.getAuthority()});

        Usuario prueba = instance.obtiene(id);
        assertEquals(usuario.getNombre(), prueba.getNombre());
    }

    @Test
    public void debieraCambiarRolDeUsuario() {
        log.debug("Debiera actualizar usuario");
        Organizacion organizacion = new Organizacion("TEST01", "TEST01", "TEST01");
        organizacion = organizacionDao.crea(organizacion);
        Rol rol = new Rol("ROLE_TEST");
        rol = rolDao.crea(rol);
        Rol rol2 = new Rol("ROLE_TEST2");
        rol2 = rolDao.crea(rol2);
        Usuario usuario = new Usuario("test-01@test.com", "test-01", "TEST1", "TEST");
        Long almacenId = 0l;
        actualizaUsuario:
        for (Empresa empresa : organizacion.getEmpresas()) {
            for (Almacen almacen : empresa.getAlmacenes()) {
                almacenId = almacen.getId();
                break actualizaUsuario;
            }
        }
        usuario = instance.crea(usuario, almacenId, new String[]{rol.getAuthority()});
        Long id = usuario.getId();

        Usuario result = instance.obtiene(id);
        assertEquals(usuario, result);
        assertTrue(result.getRoles().contains(rol));

        result.setNombre("PRUEBA");
        instance.actualiza(result, almacenId, new String[]{rol2.getAuthority()});

        Usuario prueba = instance.obtiene(id);
        assertEquals(result.getNombre(), prueba.getNombre());
        assertTrue(result.getRoles().contains(rol2));
    }

    /**
     * Test of elimina method, of class UsuarioDao.
     */
    @Test(expected = UltimoException.class)
    public void noDebieraEliminarUsuario() throws UltimoException {
        log.debug("Debiera actualizar usuario");
        Organizacion organizacion = new Organizacion("TEST01", "TEST01", "TEST01");
        organizacion = organizacionDao.crea(organizacion);
        Rol rol = new Rol("ROLE_TEST");
        rol = rolDao.crea(rol);
        Usuario usuario = new Usuario("test-01@test.com", "test-01", "TEST1", "TEST");
        Long almacenId = 0l;
        actualizaUsuario:
        for (Empresa empresa : organizacion.getEmpresas()) {
            for (Almacen almacen : empresa.getAlmacenes()) {
                almacenId = almacen.getId();
                break actualizaUsuario;
            }
        }
        usuario = instance.crea(usuario, almacenId, new String[]{rol.getAuthority()});
        Long id = usuario.getId();

        instance.elimina(id);
        fail("Debio lanzar la excepcion de ultimo usuario");
    }

    /**
     * Test of elimina method, of class UsuarioDao.
     */
    @Test
    public void debieraEliminarUsuario() throws UltimoException {
        log.debug("Debiera actualizar usuario");
        Organizacion organizacion = new Organizacion("TEST01", "TEST01", "TEST01");
        organizacion = organizacionDao.crea(organizacion);
        Rol rol = new Rol("ROLE_TEST");
        rol = rolDao.crea(rol);
        Usuario usuario = new Usuario("test-01@test.com", "test-01", "TEST1", "TEST");
        Usuario usuario2 = new Usuario("test-02@test.com", "test-02", "TEST2", "TEST");
        Long almacenId = 0l;
        actualizaUsuario:
        for (Empresa empresa : organizacion.getEmpresas()) {
            for (Almacen almacen : empresa.getAlmacenes()) {
                almacenId = almacen.getId();
                break actualizaUsuario;
            }
        }
        usuario = instance.crea(usuario, almacenId, new String[]{rol.getAuthority()});
        instance.crea(usuario2, almacenId, new String[]{rol.getAuthority()});
        Long id = usuario.getId();

        String nombre = instance.elimina(id);
        assertEquals(usuario.getUsername(), nombre);

        Usuario result = instance.obtiene(id);
        assertNull(result);
    }

    @Test
    public void debieraMostrarLosUsuariosFiltradosPorEmpresa() {
        log.debug("Mostrar los usuarios filtrados por empresa");
        Organizacion organizacion = new Organizacion("TEST01", "TEST01", "TEST01");
        organizacion = organizacionDao.crea(organizacion);
        Empresa empresa1 = null;
        for (Empresa empresa : organizacion.getEmpresas()) {
            empresa1 = empresa;
        }
        Empresa empresa = new Empresa("TEST01", "TEST01", "TEST 01", organizacion);
        empresa = empresaDao.crea(empresa);

        Rol rol = new Rol("ROLE_TEST");
        rol = rolDao.crea(rol);
        for (int i = 0; i < 30; i++) {
            Usuario usuario = new Usuario("test-a" + i + "@test.com", "test-" + i, "TEST " + i, "TEST");
            Long almacenId = 0l;
            actualizaUsuario:
            for (Almacen almacen : empresa1.getAlmacenes()) {
                almacenId = almacen.getId();
                break actualizaUsuario;
            }
            instance.crea(usuario, almacenId, new String[]{rol.getAuthority()});
        }

        for (int i = 0; i < 20; i++) {
            Usuario usuario = new Usuario("test-b" + i + "@test.com", "test-" + i, "TEST " + i, "TEST");
            Long almacenId = 0l;
            actualizaUsuario:
            for (Almacen almacen : empresa.getAlmacenes()) {
                almacenId = almacen.getId();
                break actualizaUsuario;
            }
            instance.crea(usuario, almacenId, new String[]{rol.getAuthority()});
        }

        Map<String, Object> params = new HashMap<>();
        params.put("empresa", empresa.getId());
        Map<String, Object> result = instance.lista(params);
        List<Usuario> usuarios = (List<Usuario>) result.get("usuarios");
        Long cantidad = (Long) result.get("cantidad");
        assertEquals(10, usuarios.size());
        assertTrue(20 <= cantidad);

        params.put("empresa", empresa1.getId());
        result = instance.lista(params);
        usuarios = (List<Usuario>) result.get("usuarios");
        cantidad = (Long) result.get("cantidad");
        assertEquals(10, usuarios.size());
        assertTrue(30 <= cantidad);
    }
}
