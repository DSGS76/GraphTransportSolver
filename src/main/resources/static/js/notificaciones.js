/**
 * Sistema de notificaciones personalizadas tipo toast
 * VERSIÓN SINGLETON - Evita redeclaración
 */

// ===== PROTECCIÓN CONTRA CARGA MÚLTIPLE =====
if (typeof window.Notificaciones !== 'undefined') {
    console.warn('⚠️ Sistema de notificaciones ya está cargado, se omite redeclaración');
} else {
    (function() {
        'use strict';

        class NotificationSystem {
            constructor() {
                this.container = null;
                this.MAX_NOTIFICATIONS = 3;
                this.init();
            }

            init() {
                if (!document.getElementById('notification-container')) {
                    this.container = document.createElement('div');
                    this.container.id = 'notification-container';
                    this.container.className = 'notification-container';
                    document.body.appendChild(this.container);
                } else {
                    this.container = document.getElementById('notification-container');
                }
            }

            limitarNotificaciones() {
                const notifications = this.container.querySelectorAll('.notification');
                if (notifications.length >= this.MAX_NOTIFICATIONS) {
                    const oldestNotification = notifications[0];
                    this.close(oldestNotification);
                }
            }

            show(message, type = 'info', duration = 4000) {
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

                if (duration > 0) {
                    const progressBar = notification.querySelector('.notification-progress');
                    setTimeout(() => {
                        progressBar.style.transition = `width ${duration}ms linear`;
                        progressBar.style.width = '0%';
                    }, 50);

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

            success(message, duration = 4000) {
                return this.show(message, 'success', duration);
            }

            error(message, duration = 5000) {
                const notification = this.show(message, 'error', duration);
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

            showWithTitle(title, message, type = 'info', duration = 4000) {
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

                if (duration > 0) {
                    const progressBar = notification.querySelector('.notification-progress');
                    setTimeout(() => {
                        progressBar.style.transition = `width ${duration}ms linear`;
                        progressBar.style.width = '0%';
                    }, 50);

                    setTimeout(() => {
                        this.close(notification);
                    }, duration);
                }

                return notification;
            }

            confirm(titulo, mensaje = '', encabezado = 'Confirmación', textoConfirmar = 'Confirmar', textoCancelar = 'Cancelar') {
                return new Promise((resolve) => {
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

                    document.body.appendChild(modalDiv);

                    const cerrarModal = (confirmado) => {
                        modalDiv.classList.add('cerrando');
                        setTimeout(() => {
                            if (modalDiv.parentElement) {
                                modalDiv.remove();
                            }
                            resolve(confirmado);
                        }, 300);
                    };

                    const btnConfirmar = modalDiv.querySelector('[data-accion="confirmar"]');
                    const btnCancelar = modalDiv.querySelector('[data-accion="cancelar"]');

                    btnConfirmar.addEventListener('click', () => cerrarModal(true));
                    btnCancelar.addEventListener('click', () => cerrarModal(false));

                    const handleKeydown = (e) => {
                        if (e.key === 'Escape') {
                            document.removeEventListener('keydown', handleKeydown);
                            cerrarModal(false);
                        }
                    };
                    document.addEventListener('keydown', handleKeydown);

                    modalDiv.addEventListener('click', (e) => {
                        if (e.target === modalDiv) {
                            cerrarModal(false);
                        }
                    });

                    setTimeout(() => {
                        modalDiv.classList.add('mostrar');
                    }, 10);

                    setTimeout(() => {
                        btnCancelar.focus();
                    }, 100);
                });
            }
        }

        // Crear instancia global
        window.Notificaciones = new NotificationSystem();
        console.log('✅ Sistema de notificaciones inicializado');

    })();
}

// Exportar para uso en módulos (si aplica)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = window.Notificaciones;
}