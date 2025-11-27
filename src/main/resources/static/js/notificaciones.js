/**
 * Sistema de notificaciones personalizadas tipo toast
 */
class NotificationSystem {
    constructor() {
        this.container = null;
        this.MAX_NOTIFICATIONS = 3; // Máximo de notificaciones visibles al mismo tiempo
        this.init();
    }

    init() {
        // Crear contenedor de notificaciones si no existe
        if (!document.getElementById('notification-container')) {
            this.container = document.createElement('div');
            this.container.id = 'notification-container';
            this.container.className = 'notification-container';
            document.body.appendChild(this.container);
        } else {
            this.container = document.getElementById('notification-container');
        }
    }

    /**
     * Verifica y elimina notificaciones antiguas si se supera el límite
     */
    limitarNotificaciones() {
        const notifications = this.container.querySelectorAll('.notification');

        // Si hay más notificaciones que el máximo permitido
        if (notifications.length >= this.MAX_NOTIFICATIONS) {
            // Eliminar la notificación más antigua (la primera)
            const oldestNotification = notifications[0];
            this.close(oldestNotification);
        }
    }

    /**
     * Muestra una notificación
     * @param {string} message - Mensaje a mostrar
     * @param {string} type - Tipo de notificación: 'success', 'error', 'warning', 'info'
     * @param {number} duration - Duración en milisegundos (0 = no se cierra automáticamente)
     */
    show(message, type = 'info', duration = 4000) {
        // Limitar cantidad de notificaciones antes de agregar una nueva
        this.limitarNotificaciones();

        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;

        // Icono según el tipo
        const icons = {
            success: '✅',
            error: '❌',
            warning: '⚠️',
            info: 'ℹ️'
        };

        notification.innerHTML = `
            <div class="notification-icon">${icons[type]}</div>
            <div class="notification-content">
                <div class="notification-message">${message}</div>
            </div>
            <button class="notification-close" aria-label="Cerrar">×</button>
            <div class="notification-progress"></div>
        `;

        // Agregar al contenedor
        this.container.appendChild(notification);

        // Animación de entrada
        setTimeout(() => {
            notification.classList.add('notification-show');
        }, 10);

        // Botón de cerrar
        const closeBtn = notification.querySelector('.notification-close');
        closeBtn.addEventListener('click', () => {
            this.close(notification);
        });

        // Barra de progreso animada
        if (duration > 0) {
            const progressBar = notification.querySelector('.notification-progress');

            // Iniciar animación de la barra de progreso
            setTimeout(() => {
                progressBar.style.transition = `width ${duration}ms linear`;
                progressBar.style.width = '0%';
            }, 50);

            // Auto-cerrar cuando termine la duración
            setTimeout(() => {
                this.close(notification);
            }, duration);
        }

        return notification;
    }

    close(notification) {
        notification.classList.remove('notification-show');
        notification.classList.add('notification-hide');

        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }

    // Métodos de conveniencia
    success(message, duration = 4000) {
        return this.show(message, 'success', duration);
    }

    error(message, duration = 5000) {
        const notification = this.show(message, 'error', duration);
        // Agregar efecto de sacudida a errores críticos
        setTimeout(() => {
            notification.classList.add('shake');
        }, 100);
        return notification;
    }

    warning(message, duration = 4000) {
        return this.show(message, 'warning', duration);
    }

    info(message, duration = 4000) {
        return this.show(message, 'info', duration);
    }

    /**
     * Muestra una notificación con título
     */
    showWithTitle(title, message, type = 'info', duration = 4000) {
        // Limitar cantidad de notificaciones antes de agregar una nueva
        this.limitarNotificaciones();

        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;

        const icons = {
            success: '✅',
            error: '❌',
            warning: '⚠️',
            info: 'ℹ️'
        };

        notification.innerHTML = `
            <div class="notification-icon">${icons[type]}</div>
            <div class="notification-content">
                <div class="notification-title">${title}</div>
                <div class="notification-message">${message}</div>
            </div>
            <button class="notification-close" aria-label="Cerrar">×</button>
            <div class="notification-progress"></div>
        `;

        this.container.appendChild(notification);

        setTimeout(() => {
            notification.classList.add('notification-show');
        }, 10);

        const closeBtn = notification.querySelector('.notification-close');
        closeBtn.addEventListener('click', () => {
            this.close(notification);
        });

        // Barra de progreso animada
        if (duration > 0) {
            const progressBar = notification.querySelector('.notification-progress');

            // Iniciar animación de la barra de progreso
            setTimeout(() => {
                progressBar.style.transition = `width ${duration}ms linear`;
                progressBar.style.width = '0%';
            }, 50);

            // Auto-cerrar cuando termine la duración
            setTimeout(() => {
                this.close(notification);
            }, duration);
        }

        return notification;
    }

    /**
     * Muestra un modal de confirmación personalizado
     * @param {string} titulo - Título del modal
     * @param {string} mensaje - Mensaje descriptivo
     * @param {string} encabezado - Encabezado del modal
     * @param {string} textoConfirmar - Texto del botón de confirmación
     * @param {string} textoCancelar - Texto del botón de cancelación
     * @returns {Promise<boolean>} - Promesa que resuelve true si se confirma, false si se cancela
     */
    confirm(titulo, mensaje = '', encabezado = 'Confirmación', textoConfirmar = 'Confirmar', textoCancelar = 'Cancelar') {
        return new Promise((resolve) => {
            // Crear el modal de confirmación
            const modalDiv = document.createElement('div');
            modalDiv.className = 'modal-confirmacion-overlay';
            modalDiv.innerHTML = `
                <div class="modal-confirmacion">
                    <div class="modal-header">
                        <h3 class="modal-titulo">${encabezado}</h3>
                    </div>
                    <div class="modal-contenido">
                        <div class="modal-icono">⚠️</div>
                        <div class="modal-texto">
                            <h4>${titulo}</h4>
                            ${mensaje ? `<p>${mensaje}</p>` : ''}
                        </div>
                    </div>
                    <div class="modal-botones">
                        <button class="btn-modal btn-cancelar" data-accion="cancelar">
                            <span class="icono-btn">✕</span>
                            ${textoCancelar}
                        </button>
                        <button class="btn-modal btn-confirmar" data-accion="confirmar">
                            <span class="icono-btn">✓</span>
                            ${textoConfirmar}
                        </button>
                    </div>
                </div>
            `;

            // Agregar al body
            document.body.appendChild(modalDiv);

            // Función para cerrar el modal
            const cerrarModal = (confirmado) => {
                modalDiv.classList.add('cerrando');
                setTimeout(() => {
                    if (modalDiv.parentElement) {
                        modalDiv.remove();
                    }
                    resolve(confirmado);
                }, 300);
            };

            // Event listeners para los botones
            const btnConfirmar = modalDiv.querySelector('[data-accion="confirmar"]');
            const btnCancelar = modalDiv.querySelector('[data-accion="cancelar"]');

            btnConfirmar.addEventListener('click', () => cerrarModal(true));
            btnCancelar.addEventListener('click', () => cerrarModal(false));

            // Cerrar con escape
            const handleKeydown = (e) => {
                if (e.key === 'Escape') {
                    document.removeEventListener('keydown', handleKeydown);
                    cerrarModal(false);
                }
            };
            document.addEventListener('keydown', handleKeydown);

            // Cerrar al hacer clic fuera del modal
            modalDiv.addEventListener('click', (e) => {
                if (e.target === modalDiv) {
                    cerrarModal(false);
                }
            });

            // Animar entrada
            setTimeout(() => {
                modalDiv.classList.add('mostrar');
            }, 10);

            // Enfocar el botón de cancelar por defecto
            setTimeout(() => {
                btnCancelar.focus();
            }, 100);
        });
    }

}

// Instancia global
const Notificaciones = new NotificationSystem();

// Exportar para uso en módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = Notificaciones;
}

